package me.sehwa.todolist.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.sehwa.todolist.exceptions.AllTasksNeedToBeDoneException;
import me.sehwa.todolist.exceptions.BreakChainBetweenTasksException;
import me.sehwa.todolist.exceptions.NoSuchTaskException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    public void TODO_생성() throws Exception {
        Optional<Task> task = makeTestTask();

        TaskDto parameter = TaskDto.builder()
                .content(task.get().getContent())
                .idGroupOfTasksToBeParent(task.get().getParentTaskIds()).build();

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void TODO_생성_예외처리() throws Exception {
        Optional<Task> task = makeTestTask();

        TaskDto parameter = TaskDto.builder()
                .content(task.get().getContent())
                .idGroupOfTasksToBeParent(task.get().getParentTaskIds()).build();

        when(taskService.createNewTaskAndTaskDependencies(any(Task.class), any(List.class)))
                .thenThrow(NoSuchTaskException.class);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).createNewTaskAndTaskDependencies(any(Task.class), any(List.class));
    }

    @Test
    public void 모든_TODO_조회하기() throws Exception {
        List<Task> content = new ArrayList<>();
        content.add(makeTestTask().get());

        Page<Task> tasks = new PageImpl<>(content);
        tasks.forEach(t -> t.setParentTasksFollowedByChildTask(new ArrayList<>()));

        when(taskService.getTasks(any(Pageable.class))).thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                .param("size", "6")
                .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").hasJsonPath())
                .andExpect(jsonPath("numberOfElements").hasJsonPath())
                .andExpect(jsonPath("pageable").hasJsonPath())
                .andExpect(jsonPath("totalPages").hasJsonPath());

        verify(taskService).getTasks(any(Pageable.class));
    }

    @Test
    public void ID로_TODO_한개_조회하기() throws Exception {
        when(taskService.getTaskById(9L)).thenReturn(makeTestTask());

        mockMvc.perform(get("/tasks/9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("parentTaskIds").hasJsonPath());

        verify(taskService).getTaskById(9L);
    }

    @Test
    public void 잘못된_ID로_TODO_한개_조회_예외처리() throws Exception {
        when(taskService.getTaskById(9L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/9"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(9L);
    }

    @Test
    public void TODO_수정하기() throws Exception {
        Optional<Task> task = makeTestTask();
        when(taskService.getTaskById(3L)).thenReturn(task);
        when(taskService.updateTask(any(), any())).thenReturn(task.get());

        TaskDto parameter = TaskDto.builder()
                .content(task.get().getContent())
                .idGroupOfTasksToBeParent(task.get().getParentTaskIds()).build();

        mockMvc.perform(put("/tasks/3")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").hasJsonPath());

        verify(taskService).getTaskById(3L);
        verify(taskService).updateTask(any(), any());
    }

    @Test
    public void 없는_TODO_수정하기() throws Exception {
        Optional<Task> task = makeTestTask();
        when(taskService.getTaskById(3L)).thenReturn(Optional.empty());

        TaskDto parameter = TaskDto.builder()
                .content(task.get().getContent())
                .idGroupOfTasksToBeParent(task.get().getParentTaskIds()).build();

        mockMvc.perform(put("/tasks/3")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(3L);
    }

    @Test
    public void 모든_참조하는_todo_미완료일때_수정시도_예외처리() throws Exception {
        Optional<Task> task = makeTestTask();
        when(taskService.getTaskById(3L)).thenReturn(task);
        when(taskService.setTaskDone(any())).thenThrow(AllTasksNeedToBeDoneException.class);

        TaskDto parameter = TaskDto.builder()
                .content(task.get().getContent())
                .idGroupOfTasksToBeParent(task.get().getParentTaskIds())
                .status(TaskStatus.DONE)
                .updateOnlyForStatus(true).build();

        mockMvc.perform(put("/tasks/3")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).getTaskById(3L);
        verify(taskService).setTaskDone(any());
    }

    @Test
    public void TODO_삭제하기() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(makeTestTask());

        mockMvc.perform(delete("/tasks/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(taskService).getTaskById(1L);
    }

    @Test
    public void 없는_TODO_삭제하기_예외처리() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/tasks/1"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(1L);
    }

    @Test
    public void TODO_삭제안되는경우_예외던지기() throws Exception {
        when(taskService.getTaskById(any())).thenReturn(makeTestTask());
        when(taskService.removeTask(any())).thenThrow(BreakChainBetweenTasksException.class);

        mockMvc.perform(delete("/tasks/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).getTaskById(any());
        verify(taskService).removeTask(any());
    }

    private Optional<Task> makeTestTask() {
        List<Long> parentTaskIds = new ArrayList<>();
        parentTaskIds.add(8L);
        parentTaskIds.add(9L);

        Task task = Task.builder()
                .id(1L)
                .status(TaskStatus.TODO)
                .content("todo 1")
                .createdAt(LocalDateTime.now())
                .parentTaskIds(parentTaskIds).build();

        task.setParentTasksFollowedByChildTask(new ArrayList<>());
        task.setChildTasksFollowingParentTask(new ArrayList<>());
        task.setParentTaskIds(new ArrayList<>());
        task.setParentTaskIdsString("");

        return Optional.of(task);
    }
}
