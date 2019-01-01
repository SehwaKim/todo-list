package me.sehwa.todolist.tasks;

import me.sehwa.todolist.exceptions.NoSuchTaskException;
import me.sehwa.todolist.taskDependencies.TaskDependencyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
public class TaskServiceTest {

    @TestConfiguration
    static class TaskServiceTestContextConfiguration {

        @Bean
        public TaskService taskService() {
            return new TaskService();
        }
    }

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private TaskDependencyRepository taskDependencyRepository;

    @Test(expected = NoSuchTaskException.class)
    public void 존재하지않는_TODO_참조시_예외발생시키기() {
        Task task = mock(Task.class);

//        when(taskRepository.save(task)).thenReturn()
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        List<Long> idGroupOfTasksToBeParent = Collections.singletonList(1L);

        taskService.createNewTaskAndTaskDependencies(task, idGroupOfTasksToBeParent);
        verify(taskRepository, atLeastOnce()).findById(2L);
    }

//    @Test
//    public
}
