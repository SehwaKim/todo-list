package me.sehwa.todolist.tasks;

import me.sehwa.todolist.taskDependencies.TaskDependency;
import me.sehwa.todolist.taskDependencies.TaskDependencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Transactional
    public void createNewTaskAndDependencies(Task task, List<Long> newParentTaskIDs) {
        Task savedTask = taskRepository.save(task);

        if (!newParentTaskIDs.isEmpty()) {
            for (Long newParentId : newParentTaskIDs) {
                Optional<Task> byId = taskRepository.findById(newParentId);
                if (!byId.isPresent()) {
                    throw new RuntimeException();
                }

                TaskDependency dependency = TaskDependency.builder()
                                            .nextTask(savedTask).previousTask(byId.get()).build();
                taskDependencyRepository.save(dependency);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Task> getTasks(int page, int size) {
        // TODO 파라미터로 pageable 전달해야함
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public void updateTask(Task existingTask, TaskDto taskDto) {
        List<TaskDependency> existingParentTaskIDs = existingTask.getPreviousTaskIDs();
        Set<Long> filter = new HashSet<>();
        existingParentTaskIDs.forEach(p -> filter.add(p.getId()));

        List<Long> newParentTaskIDs = taskDto.getParentTaskIDs();

        // TODO 기존 조건을 없애는건 괜찮지만 새 조건을 추가할 때 순환참조가 일어날 수 있다
        // TODO 서로에게 존재하는거는 스킵하고 지워진건 taskDependency 삭제시키고
        // TODO 새로운건 taskDependency 생성시키는데 "순환참조 안되는거 맞는지" 검사해야함
        // TODO 만약 순환참조되면 엑셉션 발생시키기

        for (Long newParentTaskId : newParentTaskIDs) {
            boolean newId = filter.add(newParentTaskId);
            if (newId) {
                Optional<Task> byId = taskRepository.findById(newParentTaskId);
                if (!byId.isPresent()) {
                    throw new RuntimeException(); // 존재하지 않는 task 를 부모 task 로 삼으려고 한다 익셉션
                }

                // 그 다음 순환참조 검사 이 parentId로 할라고 하는 것이 내 자식은 아닌지.. 검사를...
                Set<Long> visitedIDs = new HashSet<>();
                boolean containsAnyAsChild =
                        searchAllChildTasksAndCompare(existingTask.getNextTaskIDs(), newParentTaskId, visitedIDs);

                if (containsAnyAsChild) {
                    // 내 자식들을 뒤졌는데 일치하는 아이디가 있으면 익셉션
                    // 부모로 삼으려고 하는데 알고보니 내 자식이었으면 순환참조가 일어나게 되니까
                    throw new RuntimeException();
                }

                // 문제없으면 새로운 의존관계 저장
                TaskDependency taskDependency =
                        TaskDependency.builder().previousTask(byId.get()).nextTask(existingTask).build();
                taskDependencyRepository.save(taskDependency);
            }
            filter.remove(newParentTaskId);
        }

        // 기존 조건 없애기 (수정시 지우려고 의도한 조건)
        filter.forEach(parentTaskId ->
                taskDependencyRepository.deleteByChildIdAndParentId(existingTask.getId(), parentTaskId)
        );

        existingTask.setContent(taskDto.getContent());
        saveWithUpdatedTime(existingTask);
    }

    private boolean searchAllChildTasksAndCompare(List<TaskDependency> nextTaskIDs, Long newParentTaskId, Set<Long> visitedIDs) {
        if (nextTaskIDs.isEmpty()) {
            return false;
        }

        boolean containsAnyAsChild = false;

        for (TaskDependency dependency : nextTaskIDs) {
            Long existingChildTaskId = dependency.getNextTask().getId();

            if (visitedIDs.contains(existingChildTaskId)) {
                continue;
            }
            if (newParentTaskId.equals(existingChildTaskId)) {
                containsAnyAsChild = true;
                break;
            }

            visitedIDs.add(existingChildTaskId);
            containsAnyAsChild = searchAllChildTasksAndCompare(dependency.getNextTask().getNextTaskIDs(), newParentTaskId, visitedIDs);
            if (containsAnyAsChild) {
                break;
            }
        }

        return containsAnyAsChild;
    }

    @Transactional
    public void setTaskDone(Task existingTask) {
        boolean allParentTasksDone = existingTask.getPreviousTaskIDs()
                            .stream()
                            .allMatch(previousID -> previousID.getPreviousTask().getStatus() == TaskStatus.DONE);

        if (!allParentTasksDone) {
            throw new RuntimeException(); // TODO 이거말고 내가 익셉션 정의해야할거같은데
        }

        saveWithUpdatedTime(existingTask);
    }

    @Transactional
    public void setTaskToDo(Task existingTask) {
        setAllChildTasksTodoAndSave(existingTask.getNextTaskIDs());
        saveWithUpdatedTime(existingTask);
    }

    private void setAllChildTasksTodoAndSave(List<TaskDependency> nextTaskIDs) {
        if(nextTaskIDs.isEmpty()) return;

        for (TaskDependency nextTaskID : nextTaskIDs) {
            Task nextTask = nextTaskID.getNextTask();

            if (nextTask.getStatus() == TaskStatus.TODO) {
                continue;
            }
            nextTask.setStatus(TaskStatus.TODO);
            taskRepository.save(nextTask);
            setAllChildTasksTodoAndSave(nextTask.getNextTaskIDs());
        }
    }

    private void saveWithUpdatedTime(Task existingTask) {
        existingTask.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(existingTask);
    }

    @Transactional
    public void removeTask(Task existingTask) {
        // TODO 만약 이 task 의 바로 아래 자식이 하나라도 있으면 (next 가 있으면) 삭제할수 없다 익셉션 발생
        if (!existingTask.getNextTaskIDs().isEmpty()) {
            throw new RuntimeException();
        }
        taskDependencyRepository.deleteAllByNextTaskId(existingTask.getId());
        taskRepository.delete(existingTask);
    }
}
