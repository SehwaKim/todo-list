package me.sehwa.todolist.tasks;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @OneToMany(mappedBy = "childTask", fetch = FetchType.LAZY)
    private List<TaskDependency> parentTasksFollowedByChildTask = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "parentTask", fetch = FetchType.LAZY)
    private List<TaskDependency> childTasksFollowingParentTask = new ArrayList<>();

    @Transient
    private List<Long> parentTaskIds = new ArrayList<>();

    @Transient
    private String parentTaskIdsString;
}
