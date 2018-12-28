package me.sehwa.todolist.exceptions;

public class BreakChainBetweenTasksException extends RuntimeException {
    public BreakChainBetweenTasksException() {
        super("다른 TODO로부터 참조되고 있는 TODO는 삭제할 수 없습니다.");
    }
}
