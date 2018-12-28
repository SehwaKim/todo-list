package me.sehwa.todolist.taskDependencies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sehwa.todolist.tasks.Task;

import javax.persistence.*;

@Entity
@Table(name = "task_dependency")
@Getter
@Builder @AllArgsConstructor
@NoArgsConstructor
public class TaskDependency {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // lazy 일 경우라도 parentTask.getId() 했을때 쿼리문 실행안되야되는데 확인해봐야함
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_task_id")
    private Task childTask;

    public void setParentTask(Task parentTask) {
        if (this.parentTask != null) {
            this.parentTask.getChildTasksFollowingParentTask().remove(this);
        }
        this.parentTask = parentTask;
        parentTask.getChildTasksFollowingParentTask().add(this);
    }

    public void setChildTask(Task childTask) {
        if (this.childTask != null) {
            this.childTask.getParentTasksFollowedByChildTask().remove(this);
        }
        this.childTask = childTask;
        childTask.getParentTasksFollowedByChildTask().add(this);
    }
}
