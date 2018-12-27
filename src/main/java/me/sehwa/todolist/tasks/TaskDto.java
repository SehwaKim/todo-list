package me.sehwa.todolist.tasks;

import lombok.*;

import java.util.List;

@Getter @Setter
@ToString
@Builder @AllArgsConstructor @NoArgsConstructor
public class TaskDto {

    private String content;
    private List<Long> parentTaskIDs;
    private boolean done;
    private boolean updateStatusOnly;
}
