package me.sehwa.todolist.exceptions;

public class CircularReferenceException extends RuntimeException {
    public CircularReferenceException() {
        super("이 TODO를 참조하고 있는 TODO는 조건으로 추가할 수 없습니다.");
    }
}
