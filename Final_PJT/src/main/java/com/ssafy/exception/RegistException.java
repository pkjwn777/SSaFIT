package com.ssafy.exception;

public class RegistException extends Exception {
    public RegistException() {
        super("해당 아이디는 사용할 수 없습니다. 다른 아이디를 사용해주세요.");
    }

    public RegistException(String message) {
        super(message);
    }
}
