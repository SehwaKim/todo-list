package com.todolist.todolist.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "task_dependency")
@Getter
@Builder @AllArgsConstructor
@NoArgsConstructor
public class TaskDependency {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @JsonBackReference
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
