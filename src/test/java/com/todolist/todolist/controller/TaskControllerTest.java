package com.todolist.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.todolist.controller.api.TaskController;
import com.todolist.todolist.domain.Task;
import com.todolist.todolist.domain.TaskDto;
import com.todolist.todolist.domain.TaskStatus;
import com.todolist.todolist.exception.ExceptionType;
import com.todolist.todolist.exception.ServiceException;
import com.todolist.todolist.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        Task task = makeTestTask();

        TaskDto parameter = TaskDto.builder()
                .content(task.getContent())
                .idGroupOfTasksToBeParent(task.getParentTaskIdList()).build();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void TODO_생성_예외처리() throws Exception {
        Task task = makeTestTask();

        TaskDto parameter = TaskDto.builder()
                .content(task.getContent())
                .idGroupOfTasksToBeParent(task.getParentTaskIdList()).build();

        when(taskService.createNewTaskAndTaskDependencies(any(Task.class), any(List.class)))
                .thenThrow(new ServiceException(ExceptionType.NO_SUCH_TASK));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).createNewTaskAndTaskDependencies(any(Task.class), any(List.class));
    }

    @Test
    public void 모든_TODO_조회하기() throws Exception {
        List<Task> content = new ArrayList<>();
        content.add(makeTestTask());

        Page<Task> tasks = new PageImpl<>(content);
        tasks.forEach(t -> t.setParentTasksFollowedByChildTask(new ArrayList<>()));

        when(taskService.getTasks(1, 6)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks")
                .param("size", "6")
                .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").hasJsonPath())
                .andExpect(jsonPath("numberOfElements").hasJsonPath())
                .andExpect(jsonPath("pageable").hasJsonPath())
                .andExpect(jsonPath("totalPages").hasJsonPath());

        verify(taskService).getTasks(1, 6);
    }

    @Test
    public void ID로_TODO_한개_조회하기() throws Exception {
        when(taskService.getTaskById(9L)).thenReturn(makeTestTask());

        mockMvc.perform(get("/api/tasks/9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("parentTaskIdList").hasJsonPath());

        verify(taskService).getTaskById(9L);
    }

    @Test
    public void 잘못된_ID로_TODO_한개_조회_예외처리() throws Exception {
        when(taskService.getTaskById(9L)).thenThrow(new ServiceException(ExceptionType.NO_SUCH_TASK));

        mockMvc.perform(get("/api/tasks/9"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).getTaskById(9L);
    }

    @Test
    public void TODO_수정하기() throws Exception {
        Task task = makeTestTask();
        when(taskService.getTaskById(3L)).thenReturn(task);
        when(taskService.updateTask(any(), any())).thenReturn(task);

        TaskDto parameter = TaskDto.builder()
                .content(task.getContent())
                .idGroupOfTasksToBeParent(task.getParentTaskIdList()).build();

        mockMvc.perform(put("/api/tasks/3")
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
        Task task = makeTestTask();
        when(taskService.getTaskById(3L)).thenThrow(new ServiceException(ExceptionType.NO_SUCH_TASK));

        TaskDto parameter = TaskDto.builder()
                .content(task.getContent())
                .idGroupOfTasksToBeParent(task.getParentTaskIdList()).build();

        mockMvc.perform(put("/api/tasks/3")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(parameter)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).getTaskById(3L);
    }

    @Test
    public void 모든_참조하는_todo_미완료일때_수정시도_예외처리() throws Exception {
        Task task = makeTestTask();
        when(taskService.getTaskById(3L)).thenReturn(task);
        when(taskService.setTaskDone(any())).thenThrow(new ServiceException(ExceptionType.ALL_TASK_NEED_TO_BE_DONE));

        TaskDto parameter = TaskDto.builder()
                .content(task.getContent())
                .idGroupOfTasksToBeParent(task.getParentTaskIdList())
                .status(TaskStatus.DONE)
                .updateOnlyForStatus(true).build();

        mockMvc.perform(put("/api/tasks/3")
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

        mockMvc.perform(delete("/api/tasks/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(taskService).getTaskById(1L);
    }

    @Test
    public void 없는_TODO_삭제하기_예외처리() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new ServiceException(ExceptionType.NO_SUCH_TASK));

        mockMvc.perform(delete("/api/tasks/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).getTaskById(1L);
    }

    @Test
    public void TODO_삭제안되는경우_예외던지기() throws Exception {
        when(taskService.getTaskById(any())).thenReturn(makeTestTask());
        when(taskService.removeTask(any())).thenThrow(new ServiceException(ExceptionType.BREAK_CHAIN_BETWEEN_TASKS));

        mockMvc.perform(delete("/api/tasks/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService).getTaskById(any());
        verify(taskService).removeTask(any());
    }

    private Task makeTestTask() {
        List<Long> parentTaskIds = new ArrayList<>();
        parentTaskIds.add(8L);
        parentTaskIds.add(9L);

        Task task = Task.builder()
                .id(1L)
                .status(TaskStatus.TODO)
                .content("todo 1")
                .createdAt(LocalDateTime.now())
                .parentTaskIdList(parentTaskIds).build();

        task.setParentTasksFollowedByChildTask(new ArrayList<>());
        task.setChildTasksFollowingParentTask(new ArrayList<>());
        task.setParentTaskIdList(new ArrayList<>());
        task.setParentTaskIdsString("");

        return task;
    }
}
