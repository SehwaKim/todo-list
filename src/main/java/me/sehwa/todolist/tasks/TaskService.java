package me.sehwa.todolist.tasks;

import me.sehwa.todolist.exceptions.AllTasksNeedToBeDoneException;
import me.sehwa.todolist.exceptions.BreakChainBetweenTasksException;
import me.sehwa.todolist.exceptions.CircularReferenceException;
import me.sehwa.todolist.exceptions.NoSuchTaskException;
import me.sehwa.todolist.taskDependencies.TaskDependency;
import me.sehwa.todolist.taskDependencies.TaskDependencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            Task taskToBeParent = optionalTask.orElseThrow(NoSuchTaskException::new);

            TaskDependency dependency = TaskDependency.builder()
                                        .childTask(savedTask).parentTask(taskToBeParent).build();
            taskDependencyRepository.save(dependency);
        }

        return savedTask;
    }

    @Transactional(readOnly = true)
    public Page<Task> getTasks(Pageable pageable) {

        return taskRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {

        return taskRepository.findById(id);
    }

    @Transactional
    public Task updateTask(Task updatingTask, TaskDto taskDto) {

        List<TaskDependency> parentTasksFollowedByChildTask = updatingTask.getParentTasksFollowedByChildTask();

        Set<Long> filterComparingOldAndNew = new HashSet<>();
        parentTasksFollowedByChildTask.forEach(dependency -> filterComparingOldAndNew.add(dependency.getParentTask().getId()));

        List<Long> idGroupOfCandidatesForParentTask = taskDto.getIdGroupOfTasksToBeParent();

        for (Long id : idGroupOfCandidatesForParentTask) {

            boolean isNewlyAddedAsParent = filterComparingOldAndNew.add(id);

            if (isNewlyAddedAsParent) {
                Optional<Task> optionalTask = taskRepository.findById(id);
                Task taskToBeParent = optionalTask.orElseThrow(NoSuchTaskException::new);

                Set<Long> alreadyVisitedNodes = new HashSet<>();

                boolean isChildTask =
                        searchAllChildTasksAndCompareRecursively(id, updatingTask.getChildTasksFollowingParentTask(), alreadyVisitedNodes);

                if (isChildTask) {
                    throw new CircularReferenceException();
                }

                TaskDependency taskDependency =
                        TaskDependency.builder().parentTask(taskToBeParent).childTask(updatingTask).build();

                taskDependencyRepository.save(taskDependency);
            }

            removeParentTaskIdFromFilter(id, filterComparingOldAndNew);
        }

        deleteRemainingOldDependencies(updatingTask, filterComparingOldAndNew);

        updatingTask.setContent(taskDto.getContent());

        return saveWithUpdatedTime(updatingTask);
    }

    private void deleteRemainingOldDependencies(Task updatingTask, Set<Long> filter) {
        filter.forEach(notSelectedId ->
                taskDependencyRepository.deleteByChildTaskIdAndParentTaskId(updatingTask.getId(), notSelectedId)
        );
    }

    private void removeParentTaskIdFromFilter(Long id, Set<Long> filter) {
        filter.remove(id);
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
            throw new AllTasksNeedToBeDoneException();
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

    private Task saveWithUpdatedTime(Task existingTask) {

        existingTask.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(existingTask);
    }

    @Transactional
    public boolean removeTask(Task removingTask) {
        if (!removingTask.getChildTasksFollowingParentTask().isEmpty()) {
            throw new BreakChainBetweenTasksException();
        }

        taskDependencyRepository.deleteAllByChildTaskId(removingTask.getId());
        taskRepository.delete(removingTask);

        return true;
    }
}
