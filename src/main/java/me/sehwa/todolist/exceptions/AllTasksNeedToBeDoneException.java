package me.sehwa.todolist.exceptions;

public class AllTasksNeedToBeDoneException extends RuntimeException {
    public AllTasksNeedToBeDoneException() {
        super("모든 선행 TODO가 완료되지 않았습니다.");
    }
}
