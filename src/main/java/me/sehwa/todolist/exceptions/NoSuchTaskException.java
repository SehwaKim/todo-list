package me.sehwa.todolist.exceptions;

public class NoSuchTaskException extends RuntimeException {
    public NoSuchTaskException() {
        super("존재하지 않는 TODO입니다.");
    }
}
