package me.sehwa.todolist.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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

        Task task = Task.builder()
                .content(taskDto.getContent())
                .status(TaskStatus.TODO)
                .createdAt(LocalDateTime.now())
                .build();

        taskService.createNewTaskAndDependencies(task, taskDto.getParentTaskIDs());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getTasks(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "8") int size) {
        List<Task> tasks = taskService.getTasks(page, size);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity getTask(@PathVariable Long id) {
        Optional<Task> byId = taskService.getTaskById(id);
        return byId.isPresent() ? ResponseEntity.ok(byId.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@PathVariable Long id,
                                     @RequestBody TaskDto taskDto) {
        Optional<Task> byId = taskService.getTaskById(id);
        if (!byId.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // TODO exception 일어날 경우 따로 처리해줘야 한다
        if (taskDto.isUpdateStatusOnly()) {
            if (taskDto.isDone()) {
                taskService.setTaskDone(byId.get());
            } else {
                taskService.setTaskToDo(byId.get());
            }
        } else {
            taskService.updateTask(byId.get(), taskDto);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {
        Optional<Task> byId = taskService.getTaskById(id);
        if (!byId.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // TODO 만약 후행조건이 있다면 이 task 는 삭제할 수 없다 익셉션 발생된거 처리해주기
        taskService.removeTask(byId.get());

        return ResponseEntity.ok().build();
    }
}
