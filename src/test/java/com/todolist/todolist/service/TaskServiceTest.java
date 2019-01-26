package com.todolist.todolist.service;

import com.todolist.todolist.domain.Task;
import com.todolist.todolist.domain.TaskDependency;
import com.todolist.todolist.exception.ServiceException;
import com.todolist.todolist.repository.TaskDependencyRepository;
import com.todolist.todolist.repository.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    public void 새_TODO_추가() {
        Task task = Task.builder().id(1L).content("todo 1").createdAt(LocalDateTime.now()).build();
        when(taskRepository.save(task)).thenReturn(task);

        Task savedTask = taskService.createNewTaskAndTaskDependencies(task, new ArrayList<>());

        assertThat(savedTask.getId()).isEqualTo(task.getId());
        assertThat(savedTask.getContent()).isEqualTo(task.getContent());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    public void 새_TODO_추가할때_참조TODO_의존관계들도_같이_추가() {
        when(taskRepository.save(any(Task.class))).thenReturn(mock(Task.class));
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(mock(Task.class)));
        when(taskDependencyRepository.save(any(TaskDependency.class))).thenReturn(mock(TaskDependency.class));

        List<Long> idGroupOfTasksToBeParent = new ArrayList<>();
        idGroupOfTasksToBeParent.add(11L);
        idGroupOfTasksToBeParent.add(12L);
        idGroupOfTasksToBeParent.add(13L);

        taskService.createNewTaskAndTaskDependencies(mock(Task.class), idGroupOfTasksToBeParent);

        verify(taskRepository).save(any(Task.class));
        verify(taskRepository, times(3)).findById(any());
        verify(taskDependencyRepository, times(3)).save(any(TaskDependency.class));
    }

    @Test(expected = ServiceException.class)
    public void 새_TODO_추가시_없는TODO를_참조하려고할때_예외발생처리() {
        when(taskRepository.save(any(Task.class))).thenReturn(mock(Task.class));
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        List<Long> idGroupOfTasksToBeParent = new ArrayList<>();
        idGroupOfTasksToBeParent.add(11L);

        taskService.createNewTaskAndTaskDependencies(mock(Task.class), idGroupOfTasksToBeParent);

        verify(taskRepository).save(any(Task.class));
        verify(taskRepository).findById(11L);
    }

    @Test
    public void 페이징처리된_모든_TODO_가져오기() {
        int page = 3;
        int size = 6;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Task> listOfMock = new ArrayList<>();
        listOfMock.add(mock(Task.class));
        listOfMock.add(mock(Task.class));
        listOfMock.add(mock(Task.class));

        Page<Task> pagedTasks = new PageImpl<Task>(listOfMock, pageable, 6);
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(pagedTasks);

        Page<Task> resultTasks = taskService.getTasks(pageable);
        assertThat(resultTasks.getContent().size()).isEqualTo(3);
        assertThat(resultTasks.getPageable().getOffset()).isEqualTo(page * size);
        assertThat(resultTasks.getPageable().getPageSize()).isEqualTo(size);
        assertThat(resultTasks.getPageable().getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "id"));

        verify(taskRepository).findAll(any(Pageable.class));
    }

    @Test
    public void TODO_한개_가져오기() {
        Task task = Task.builder().id(10L).content("todo 1").createdAt(LocalDateTime.now()).build();

        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));

        Optional<Task> optionalTask = taskService.getTaskById(10L);
        assertThat(optionalTask.get().getId()).isEqualTo(10L);
        assertThat(optionalTask.get().getContent()).isEqualTo("todo 1");

        verify(taskRepository).findById(10L);
    }

    @Test
    public void TODO_삭제하기() {
        doNothing().when(taskDependencyRepository).deleteAllByChildTaskId(any(Long.class));
        doNothing().when(taskRepository).delete(any(Task.class));

        assertThat(taskService.removeTask(mock(Task.class))).isTrue();

        verify(taskDependencyRepository, times(1)).deleteAllByChildTaskId(any(Long.class));
        verify(taskRepository, times(1)).delete(any(Task.class));
    }

    @Test(expected = ServiceException.class)
    public void 다른TODO들에게_참조당하는_TODO_삭제하려고할때_예외발생처리() {
        Task mockTask = mock(Task.class);
        List<TaskDependency> mockList = mock(List.class);

        when(mockTask.getChildTasksFollowingParentTask()).thenReturn(mockList);
        when(mockList.isEmpty()).thenReturn(false);

        taskService.removeTask(mockTask);

        verify(mockTask, times(1)).getChildTasksFollowingParentTask();
        verify(mockList, times(1)).isEmpty();
    }


}
