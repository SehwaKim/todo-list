package me.sehwa.todolist.tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import me.sehwa.todolist.taskDependencies.TaskDependency;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder @AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private TaskStatus status;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "nextTask", fetch = FetchType.LAZY)
    private List<TaskDependency> previousTaskIDs = new ArrayList<>();

    @OneToMany(mappedBy = "previousTask", fetch = FetchType.LAZY)
    private List<TaskDependency> nextTaskIDs = new ArrayList<>();

    public void addPreviousTaskIDs(TaskDependency taskDependency) {
        this.previousTaskIDs.add(taskDependency);
        taskDependency.setNextTask(this);
    }

    public void addNextTaskIDs(TaskDependency taskDependency) {
        this.nextTaskIDs.add(taskDependency);
        taskDependency.setPreviousTask(this);
    }
}
