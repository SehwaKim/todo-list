package me.sehwa.todolist.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Task {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private TaskStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Task(String content) {
        this.content = content;
        this.status = TaskStatus.TODO;
        this.createdAt = LocalDateTime.now();
    }
}
