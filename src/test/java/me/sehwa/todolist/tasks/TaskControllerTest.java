package me.sehwa.todolist.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        String content = "today's homework";
        List<Long> parentTaskIdGroup = new ArrayList<>();
        parentTaskIdGroup.add(8L);
        parentTaskIdGroup.add(9L);
        TaskDto taskDto = TaskDto.builder()
                .content(content)
                .idGroupOfTasksToBeParent(parentTaskIdGroup).build();

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    /*@Test
    public void 모든_TODO_조회하기() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .param("size", "8")
                .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk());
    }*/

    /*@Test
    public void ID로_TODO_한개_조회하기() throws Exception {
        mockMvc.perform(get("/api/tasks/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 잘못된_ID로_TODO_한개_조회하기() throws Exception {
        mockMvc.perform(get("/api/tasks/a"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }*/

    /*@Test
    public void TODO_수정하기() throws Exception {
        String content = "tomorrow's homework";
        List<Long> parentTaskIdGroup = new ArrayList<>();
        parentTaskIdGroup.add(1L);
        parentTaskIdGroup.add(2L);
        TaskDto taskDto = TaskDto.builder()
                .content(content)
                .idGroupOfTasksToBeParent(parentTaskIdGroup).status(TaskStatus.DONE).build();

        mockMvc.perform(get("/api/tasks/3")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andDo(print())
                .andExpect(status().isOk());
    // TODO notFound 랑 익셉션 났을경우에도 테스트 해줘야하는데
    }

    @Test
    public void TODO_삭제하기() throws Exception {
        mockMvc.perform(get("/api/tasks/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }*/
}
