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

    @ManyToOne(fetch = FetchType.LAZY) // lazy 일 경우라도 previousTask.getId() 했을때 쿼리문 실행안되야되는데 확인해봐야함
    @JoinColumn(name = "previous_id")
    private Task previousTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id")
    private Task nextTask;

    public void setPreviousTask(Task previousTask) {
        if (this.previousTask != null) {
            this.previousTask.getNextTaskIDs().remove(this);
        }
        this.previousTask = previousTask;
        previousTask.getNextTaskIDs().add(this);
    }

    public void setNextTask(Task nextTask) {
        if (this.nextTask != null) {
            this.nextTask.getPreviousTaskIDs().remove(this);
        }
        this.nextTask = nextTask;
        nextTask.getPreviousTaskIDs().add(this);
    }
}
