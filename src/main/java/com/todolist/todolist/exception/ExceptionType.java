package com.todolist.todolist.exception;

public enum  ExceptionType {

    NO_SUCH_TASK("존재하지 않는 TODO입니다."),
    CIRCULAR_REFERENCE("이 TODO를 참조하고 있는 TODO는 조건으로 추가할 수 없습니다."),
    ALL_TASK_NEED_TO_BE_DONE("모든 선행 TODO가 완료되지 않았습니다."),
    BREAK_CHAIN_BETWEEN_TASKS("다른 TODO로부터 참조되고 있는 TODO는 삭제할 수 없습니다.");

    private String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
