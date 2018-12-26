package me.sehwa.todolist.tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
