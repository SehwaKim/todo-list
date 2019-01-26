package com.todolist.todolist.repository;

import com.todolist.todolist.domain.Task;
import com.todolist.todolist.domain.TaskDependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskDependencyRepositoryTest {

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    private List<Task> tasksForTest = new ArrayList<>();

    @Before
    public void setSomeTasks() {
        tasksForTest.add(taskRepository.save(Task.builder().id(1L).build()));
        tasksForTest.add(taskRepository.save(Task.builder().id(2L).build()));
        tasksForTest.add(taskRepository.save(Task.builder().id(3L).build()));
    }

    @Test
    public void testNotNull() {
        assertThat(taskDependencyRepository).isNotNull();
    }

    @Test
    public void 새_TODO_의존관계_생성하기() {
        Task parent = tasksForTest.get(0);
        Task child = tasksForTest.get(1);

        TaskDependency saved = taskDependencyRepository.save(createNewDependency(parent, child));

        assertThat(taskDependencyRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    public void 부모_TODO_기준으로_자식_TODO들_가져오기() {
        Task parent = tasksForTest.get(0);
        Task child1 = tasksForTest.get(1);
        Task child2 = tasksForTest.get(2);

        taskDependencyRepository.save(createNewDependency(parent, child1));
        taskDependencyRepository.save(createNewDependency(parent, child2));

        List<TaskDependency> all = taskDependencyRepository.findAllByParentTaskId(parent.getId());

        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    public void 자식_TODO_기준으로_부모_TODO들_가져오기() {
        Task child = tasksForTest.get(0);
        Task parent1 = tasksForTest.get(1);
        Task parent2 = tasksForTest.get(2);

        taskDependencyRepository.save(createNewDependency(parent1, child));
        taskDependencyRepository.save(createNewDependency(parent2, child));

        List<TaskDependency> all = taskDependencyRepository.findAllByChildTaskId(child.getId());

        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    public void 특정_TODO_의존관계_한개_삭제하기() {
        Task child = tasksForTest.get(0);
        Task parent = tasksForTest.get(1);
        TaskDependency saved = taskDependencyRepository.save(createNewDependency(parent, child));

        taskDependencyRepository.deleteByChildTaskIdAndParentTaskId(child.getId(), parent.getId());

        assertThat(taskDependencyRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    public void 자식_TODO_기준으로_모든_의존관계_삭제하기() {
        Task child = tasksForTest.get(0);
        Task parent1 = tasksForTest.get(1);
        Task parent2 = tasksForTest.get(2);

        taskDependencyRepository.save(createNewDependency(parent1, child));
        taskDependencyRepository.save(createNewDependency(parent2, child));

        taskDependencyRepository.deleteAllByChildTaskId(child.getId());

        assertThat(taskDependencyRepository.findAllByChildTaskId(child.getId()).isEmpty()).isTrue();
    }

    private TaskDependency createNewDependency(Task parentTask, Task childTask) {
        return TaskDependency.builder().parentTask(parentTask).childTask(childTask).build();
    }
}
