package me.sehwa.todolist.tasks;

import me.sehwa.todolist.taskDependencies.TaskDependencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Transactional
    public void createNewTaskAndDependencies(Task task, List<Long> dependencies) {
        Task savedTask = taskRepository.save(task);
//        if (!dependencies.isEmpty()) {
//            dependencies.forEach(id->taskDependencyRepository.save(TaskDependency.builder().previous(id).next(savedTask.getId())));
//        }
    }

    @Transactional(readOnly = true)
    public List<Task> getTasks(int page, int size) {
        // TODO 파라미터로 pageable 전달해야함
        return taskRepository.findAll();
    }
}
