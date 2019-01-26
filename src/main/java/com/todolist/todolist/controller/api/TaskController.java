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

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Page<Task> tasks = taskService.getTasks(PageRequest.of(page - 1, size, sort));

        tasks.forEach(task -> {
                StringBuilder sb = new StringBuilder();

                task.getParentTasksFollowedByChildTask()
                        .forEach(dependency ->{
                                task.getParentTaskIds().add(dependency.getParentTask().getId());
                                sb.append(" @"+dependency.getParentTask().getId());
                        });

                task.setParentTaskIdsString(sb.toString());
            }
        );

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity getTask(@PathVariable Long id) {

        Optional<Task> optionalTask = taskService.getTaskById(id);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Task task = optionalTask.get();
        StringBuilder sb = new StringBuilder();
        task.getParentTasksFollowedByChildTask().forEach(dependency ->{
                task.getParentTaskIds().add(dependency.getParentTask().getId());
                sb.append(" @"+dependency.getParentTask().getId());
            }
        );
        task.setParentTaskIdsString(sb.toString());

        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@PathVariable Long id,
                                     @RequestBody TaskDto taskDto) {
        log.info(taskDto.toString());

        Optional<Task> optionalTask = taskService.getTaskById(id);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (taskDto.isUpdateOnlyForStatus()) {
            TaskDto updatedTaskDto = null;

            if (taskDto.getStatus().isDone()) {
                updatedTaskDto = taskService.setTaskDone(optionalTask.get());
            } else {
                updatedTaskDto = taskService.setTaskToDo(optionalTask.get());
            }
            return ResponseEntity.ok(updatedTaskDto);
        }


        Task updatedTask = taskService.updateTask(optionalTask.get(), taskDto);
        updatedTask.getParentTasksFollowedByChildTask()
                .forEach(
                        dependency -> updatedTask.getParentTaskIds()
                                .add(dependency.getParentTask().getId())
                );
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {

        Optional<Task> optionalTask = taskService.getTaskById(id);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        taskService.removeTask(optionalTask.get());
        return ResponseEntity.ok().build();
    }
}
