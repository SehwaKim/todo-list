package com.todolist.todolist.repository;

import com.todolist.todolist.domain.Task;
import com.todolist.todolist.domain.TaskStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testNotNull() {
        assertThat(taskRepository).isNotNull();
    }

    @Test
    public void 새_TODO_저장하기() {
        String content = "방 청소하기";
        Task saved = taskRepository.save(createNewTask(content));

        assertThat(taskRepository.existsById(saved.getId())).isTrue();
        assertThat(saved.getContent()).isEqualTo(content);
    }

    @Test
    public void 모든_TODO_가져오기() {
        List<Task> all = taskRepository.findAll();
        int size = all.size();

        taskRepository.save(createNewTask("청소기 돌리기"));
        taskRepository.save(createNewTask("이불 개기"));

        all = taskRepository.findAll();
        assertThat(all.size()).isEqualTo(size + 2);
    }

    @Test
    public void TODO_내용_수정하기() {
        Task saved = taskRepository.save(createNewTask("강아지 밥주기"));

        String content = "고양이 밥주기";
        saved.setContent(content);
        saved.setUpdatedAt(LocalDateTime.now());
        saved = taskRepository.save(saved);

        Task updated = taskRepository.findById(saved.getId()).get();
        assertThat(updated.getContent()).isEqualTo(content);
    }

    @Test
    public void TODO_상태_수정하기() {
        Task saved = taskRepository.save(createNewTask("강아지 밥주기"));
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.TODO);

        saved.setStatus(TaskStatus.DONE);
        Task updated = taskRepository.save(saved);

        assertThat(updated.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    public void TODO_삭제하기() {
        Task saved = taskRepository.save(createNewTask("쿠기 만들기"));
        assertThat(taskRepository.existsById(saved.getId())).isTrue();

        taskRepository.delete(saved);
        assertThat(taskRepository.existsById(saved.getId())).isFalse();
    }

    private Task createNewTask(String content) {
        return Task.builder().content(content).status(TaskStatus.TODO).createdAt(LocalDateTime.now()).build();
    }
}
