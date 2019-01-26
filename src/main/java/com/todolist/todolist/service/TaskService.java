package com.todolist.todolist.service;

import com.todolist.todolist.domain.Task;
import com.todolist.todolist.domain.TaskDependency;
import com.todolist.todolist.domain.TaskDto;
import com.todolist.todolist.domain.TaskStatus;
import com.todolist.todolist.exception.ExceptionType;
import com.todolist.todolist.exception.ServiceException;
import com.todolist.todolist.repository.TaskDependencyRepository;
import com.todolist.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Transactional
    public Task createNewTaskAndTaskDependencies(Task task, List<Long> idGroupOfTasksToBeParent) {
        Task savedTask = taskRepository.save(task);

        if (idGroupOfTasksToBeParent.isEmpty()) {
            return savedTask;
        }

        for (Long Id : idGroupOfTasksToBeParent) {

            Optional<Task> optionalTask = taskRepository.findById(Id);
            Task taskToBeParent = optionalTask.orElseThrow(() -> new ServiceException(ExceptionType.NO_SUCH_TASK));

            TaskDependency dependency = TaskDependency.builder()
                                        .childTask(savedTask).parentTask(taskToBeParent).build();
            taskDependencyRepository.save(dependency);
        }

        return savedTask;
    }

    @Transactional(readOnly = true)
    public Page<Task> getTasks(int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Page<Task> tasks = taskRepository.findAll(PageRequest.of(page - 1, size, sort));

        tasks.forEach(task -> task.setParentTaskIdString(makeParentIdString(task)));

        return tasks;
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {

        Optional<Task> optionalTask = taskRepository.findById(id);
        if (!optionalTask.isPresent()) {
            throw new ServiceException(ExceptionType.NO_SUCH_TASK);
        }

        Task task = optionalTask.get();
        task.setParentTaskIdString(makeParentIdString(task));
        return task;
    }

    private String makeParentIdString(Task task) {

        StringBuilder sb = new StringBuilder();

        task.getParentTasksFollowedByChildTask().forEach(dependency ->{
                    task.getParentTaskIdList().add(dependency.getParentTask().getId());
                    sb.append(" @"+dependency.getParentTask().getId());
                }
        );

        return sb.toString();
    }

    @Transactional
    public Task updateTask(Task updatingTask, TaskDto taskDto) {

        List<TaskDependency> updatedParentTaskDependency
                = getUpdatedParentTaskDependency(updatingTask, taskDto.getIdGroupOfTasksToBeParent());

        updatingTask.setParentTasksFollowedByChildTask(updatedParentTaskDependency);
        updatingTask.setContent(taskDto.getContent());

        makeParentIdList(updatingTask);

        return saveWithUpdatedTime(updatingTask);
    }

    private void makeParentIdList(Task updatingTask) {
        updatingTask.getParentTasksFollowedByChildTask()
                .forEach(
                        dependency -> updatingTask.getParentTaskIdList().add(dependency.getParentTask().getId())
                );
    }

    private List<TaskDependency> getUpdatedParentTaskDependency(Task updatingTask, List<Long> idGroupOfTasksToBeParent) {

        List<TaskDependency> updatedParentTaskDependency = new ArrayList<>();

        List<Long> newlyAddedParentTaskIds
                = getNewlyAddedParentTaskIds(updatingTask.getParentTasksFollowedByChildTask(), idGroupOfTasksToBeParent);
        List<Long> wantToDeleteParentTaskIds
                = getNotSelectedOldParentTaskIds(updatingTask.getParentTasksFollowedByChildTask(), idGroupOfTasksToBeParent);

        createNewDependenciesAndAddToList(newlyAddedParentTaskIds, updatedParentTaskDependency, updatingTask);
        deleteNotSelectedOldDependencies(wantToDeleteParentTaskIds, updatingTask);

        return updatedParentTaskDependency;
    }

    private List<Long> getNewlyAddedParentTaskIds(List<TaskDependency> currentParentTaskDependencies,
                                                  List<Long> idGroupOfTasksToBeParent) {
        List<Long> newlyAddedParentTaskIds = new ArrayList<>();

        Set<Long> filterComparingOldAndNew = new HashSet<>();
        currentParentTaskDependencies.forEach(
                dependency -> filterComparingOldAndNew.add(dependency.getParentTask().getId())
        );

        for (Long id : idGroupOfTasksToBeParent) {
            boolean newlyAddedParent = filterComparingOldAndNew.add(id);
            if (newlyAddedParent) {
                newlyAddedParentTaskIds.add(id);
            }
        }

        return newlyAddedParentTaskIds;
    }

    private List<Long> getNotSelectedOldParentTaskIds(List<TaskDependency> currentParentTaskDependencies,
                                                    List<Long> idGroupOfTasksToBeParent) {
        Set<Long> notSelectedOldParentTasks = new HashSet<>();
        currentParentTaskDependencies.forEach(
                dependency -> notSelectedOldParentTasks.add(dependency.getParentTask().getId())
        );

        for (Long id : idGroupOfTasksToBeParent) {
            notSelectedOldParentTasks.remove(id);
        }

        return new ArrayList<>(notSelectedOldParentTasks);
    }

    private void createNewDependenciesAndAddToList(List<Long> newlyAddedParentTaskIds,
                                                   List<TaskDependency> updatedParentTaskDependency,
                                                   Task updatingTask) {
        for (Long id : newlyAddedParentTaskIds) {
            Task taskToBeParent = getTaskToBeParent(id, updatingTask);
            TaskDependency dependency = TaskDependency
                                        .builder()
                                        .parentTask(taskToBeParent)
                                        .childTask(updatingTask).build();
            dependency = taskDependencyRepository.save(dependency);
            updatedParentTaskDependency.add(dependency);
        }
    }

    private void deleteNotSelectedOldDependencies(List<Long> wantToDeleteParentTaskIds,
                                                        Task updatingTask) {
        for (Long id : wantToDeleteParentTaskIds) {
            taskDependencyRepository.deleteByChildTaskIdAndParentTaskId(updatingTask.getId(), id);
        }
    }

    private Task getTaskToBeParent(Long id, Task updatingTask) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        Task task = optionalTask.orElseThrow(() -> new ServiceException(ExceptionType.NO_SUCH_TASK));

        if (isChildTaskOf(updatingTask, id)) {
            throw new ServiceException(ExceptionType.CIRCULAR_REFERENCE);
        }

        return task;
    }

    private boolean isChildTaskOf(Task updatingTask, Long id) {
        boolean isChildTask = false;

        Set<Long> alreadyVisitedNodes = new HashSet<>();

        isChildTask = searchAllChildTasksAndCompareRecursively(id,
                                                               updatingTask.getChildTasksFollowingParentTask(),
                                                               alreadyVisitedNodes);

        return isChildTask;
    }

    private boolean searchAllChildTasksAndCompareRecursively(Long idOfTaskToBeParent,
                                                             List<TaskDependency> childTasksFollowingParentTask,
                                                             Set<Long> alreadyVisitedNodes) {
        if (childTasksFollowingParentTask.isEmpty()) {
            return false;
        }

        boolean isChildTask = false;

        for (TaskDependency dependency : childTasksFollowingParentTask) {
            Long childTaskId = dependency.getChildTask().getId();

            if (alreadyVisitedNodes.contains(childTaskId)) {
                continue;
            }
            if (idOfTaskToBeParent.equals(childTaskId)) {
                isChildTask = true;
                break;
            }

            alreadyVisitedNodes.add(childTaskId);

            isChildTask = searchAllChildTasksAndCompareRecursively(idOfTaskToBeParent,
                                                    dependency.getChildTask().getChildTasksFollowingParentTask(),
                                                    alreadyVisitedNodes);
            if (isChildTask) {
                break;
            }
        }
        return isChildTask;
    }

    @Transactional
    public TaskDto setTaskDone(Task updatingTask) {
        boolean allParentTasksDone = updatingTask.getParentTasksFollowedByChildTask()
                            .stream()
                            .allMatch(dependency -> dependency.getParentTask().getStatus().isDone());

        if (!allParentTasksDone) {
            throw new ServiceException(ExceptionType.ALL_TASK_NEED_TO_BE_DONE);
        }

        updatingTask.setStatus(TaskStatus.DONE);
        Task updatedTask = saveWithUpdatedTime(updatingTask);

        return TaskDto.builder().status(updatedTask.getStatus()).build();
    }

    @Transactional
    public TaskDto setTaskToDo(Task updatingTask) {
        List<Long> affectedChildTaskIds = new ArrayList<>();
        setAllChildTasksTodoAndSaveRecursively(updatingTask.getChildTasksFollowingParentTask(), affectedChildTaskIds);

        updatingTask.setStatus(TaskStatus.TODO);
        Task updatedTask = saveWithUpdatedTime(updatingTask);

        return TaskDto.builder().status(updatedTask.getStatus()).idGroupOfChildTasksTodo(affectedChildTaskIds).build();
    }

    private void setAllChildTasksTodoAndSaveRecursively(List<TaskDependency> childTasksFollowingParentTask,
                                                        List<Long> affectedChildTaskIds) {
        if(childTasksFollowingParentTask.isEmpty()) return;

        for (TaskDependency dependency : childTasksFollowingParentTask) {
            Task childTask = dependency.getChildTask();

            if (childTask.getStatus().isTodo()) {
                continue;
            }

            childTask.setStatus(TaskStatus.TODO);
            taskRepository.save(childTask);
            affectedChildTaskIds.add(childTask.getId());
            setAllChildTasksTodoAndSaveRecursively(childTask.getChildTasksFollowingParentTask(), affectedChildTaskIds);
        }
    }

    private Task saveWithUpdatedTime(Task updatingTask) {
        updatingTask.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(updatingTask);
    }

    @Transactional
    public boolean removeTask(Task removingTask) {
        if (!removingTask.getChildTasksFollowingParentTask().isEmpty()) {
            throw new ServiceException(ExceptionType.BREAK_CHAIN_BETWEEN_TASKS);
        }

        taskDependencyRepository.deleteAllByChildTaskId(removingTask.getId());
        taskRepository.delete(removingTask);

        return true;
    }
}
