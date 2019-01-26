package com.todolist.todolist.controller.api;

import com.todolist.todolist.domain.Task;
import com.todolist.todolist.domain.TaskDto;
import com.todolist.todolist.service.TaskService;
import com.todolist.todolist.domain.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity createTask(@RequestBody TaskDto taskDto) {
        log.info(taskDto.toString());

        if (StringUtils.isEmpty(taskDto.getContent())) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Task task = Task.builder()
                .content(taskDto.getContent())
                .status(TaskStatus.TODO)
                .createdAt(LocalDateTime.now())
                .build();

        taskService.createNewTaskAndTaskDependencies(task, taskDto.getIdGroupOfTasksToBeParent());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getTasks(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "6") int size) {

        Page<Task> tasks = taskService.getTasks(page, size);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity getTask(@PathVariable Long id) {

        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@PathVariable Long id,
                                     @RequestBody TaskDto taskDto) {
        log.info(taskDto.toString());

        Task task = taskService.getTaskById(id);

        if (taskDto.isUpdateOnlyForStatus()) {
            TaskDto updatedTaskDto = null;

            if (taskDto.getStatus().isDone()) {
                updatedTaskDto = taskService.setTaskDone(task);
            } else {
                updatedTaskDto = taskService.setTaskToDo(task);
            }

            return ResponseEntity.ok(updatedTaskDto);
        }

        Task updatedTask = taskService.updateTask(task, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {

        Task task = taskService.getTaskById(id);
        taskService.removeTask(task);
        return ResponseEntity.ok().build();
    }
}
