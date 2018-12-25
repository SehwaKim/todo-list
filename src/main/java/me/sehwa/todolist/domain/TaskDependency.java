package me.sehwa.todolist.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
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
