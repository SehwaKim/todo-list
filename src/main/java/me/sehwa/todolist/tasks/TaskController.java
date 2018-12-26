package me.sehwa.todolist.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(value = "/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity createTask(@RequestBody TaskDto taskDto) {
        log.info(taskDto.toString());

        Task task = Task.builder()
                .content(taskDto.getContent())
                .status(TaskStatus.TODO)
                .createdAt(LocalDateTime.now())
                .build();

        taskService.createNewTaskAndDependencies(task, taskDto.getDependencies());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getTasks() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity getTask(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@PathVariable Long id) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {
        return null;
    }
}
