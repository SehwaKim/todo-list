package me.sehwa.todolist.tasks;

public enum TaskStatus {
    TODO, DONE;

    public boolean isDone() {
        return this == DONE;
    }

    public boolean isTodo() {
        return this == TODO;
    }
}
