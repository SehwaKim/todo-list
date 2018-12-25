package me.sehwa.todolist.taskDependencies;

import me.sehwa.todolist.tasks.Task;
import me.sehwa.todolist.tasks.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskDependencyRepositoryTest {

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void 새_TODO_의존관계_생성하기() {
        Task previous = taskRepository.getOne(1L);
        Task next = taskRepository.getOne(2L);
        TaskDependency saved = taskDependencyRepository.save(createNewDependency(previous, next));

        assertThat(taskDependencyRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    public void 선행조건_기준으로_후행조건들_가져오기() {
        Task previous = taskRepository.getOne(1L);
        Task next1 = taskRepository.getOne(3L);
        Task next2 = taskRepository.getOne(2L);
        Task next3 = taskRepository.getOne(4L);

        taskDependencyRepository.save(createNewDependency(previous, next1));
        taskDependencyRepository.save(createNewDependency(previous, next2));
        taskDependencyRepository.save(createNewDependency(previous, next3));

        List<TaskDependency> all = taskDependencyRepository.findAllByPreviousId(previous.getId());
        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    public void 후행조건_기준으로_선행조건들_가져오기() {
        Task previous1 = taskRepository.getOne(4L);
        Task previous2 = taskRepository.getOne(3L);
        Task previous3 = taskRepository.getOne(2L);
        Task next = taskRepository.getOne(1L);

        taskDependencyRepository.save(createNewDependency(previous1, next));
        taskDependencyRepository.save(createNewDependency(previous2, next));
        taskDependencyRepository.save(createNewDependency(previous3, next));

        List<TaskDependency> all = taskDependencyRepository.findAllByNextId(next.getId());
        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    public void TODO_의존관계_삭제하기() {
        Task previous = taskRepository.getOne(1L);
        Task next = taskRepository.getOne(2L);
        TaskDependency saved = taskDependencyRepository.save(createNewDependency(previous, next));
        assertThat(taskDependencyRepository.existsById(saved.getId())).isTrue();

        taskDependencyRepository.delete(saved);
        assertThat(taskDependencyRepository.existsById(saved.getId())).isFalse();
    }

    private TaskDependency createNewDependency(Task previous, Task next) {
        return TaskDependency.builder().previous(previous).next(next).build();
    }
}
