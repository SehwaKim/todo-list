package me.sehwa.todolist.tasks;

import lombok.extern.slf4j.Slf4j;
import me.sehwa.todolist.exceptions.AllTasksNeedToBeDoneException;
import me.sehwa.todolist.exceptions.BreakChainBetweenTasksException;
import me.sehwa.todolist.exceptions.NoSuchTaskException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/tasks")
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

        try {

            taskService.createNewTaskAndTaskDependencies(task, taskDto.getIdGroupOfTasksToBeParent());

        } catch (NoSuchTaskException ex) {

            Map<String, String> message = Collections.singletonMap("message", ex.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

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
        if (optionalTask.isPresent()) {
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
                try {

                    updatedTaskDto = taskService.setTaskDone(optionalTask.get());

                } catch (AllTasksNeedToBeDoneException ex) {

                    Map<String, String> message = Collections.singletonMap("message", ex.getMessage());
                    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
                }

            } else {
                updatedTaskDto = taskService.setTaskToDo(optionalTask.get());
            }

            return ResponseEntity.ok(updatedTaskDto);
        }

        try {
            Task updatedTask = taskService.updateTask(optionalTask.get(), taskDto);
            updatedTask.getParentTasksFollowedByChildTask()
                    .forEach(
                            dependency -> updatedTask.getParentTaskIds()
                                    .add(dependency.getParentTask().getId())
                    );
            return ResponseEntity.ok(updatedTask);

        } catch (RuntimeException ex) {
            Map<String, String> message = Collections.singletonMap("message", ex.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {

        Optional<Task> optionalTask = taskService.getTaskById(id);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {

            taskService.removeTask(optionalTask.get());

        } catch (BreakChainBetweenTasksException ex) {

            Map<String, String> message = Collections.singletonMap("message", ex.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }
}
