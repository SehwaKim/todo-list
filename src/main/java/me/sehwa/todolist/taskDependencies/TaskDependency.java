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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_id")
    private Task previous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id")
    private Task next;
}
