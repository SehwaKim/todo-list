package com.todolist.todolist.domain;

import com.todolist.todolist.domain.TaskStatus;
import lombok.*;

import java.util.List;

@Getter @Setter
@ToString
@Builder @AllArgsConstructor @NoArgsConstructor
public class TaskDto {

    private String content;
    private TaskStatus status;
    private List<Long> idGroupOfTasksToBeParent;
    private List<Long> idGroupOfChildTasksTodo;

    private boolean updateOnlyForStatus;
}
