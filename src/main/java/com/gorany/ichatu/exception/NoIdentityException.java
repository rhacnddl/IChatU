package com.gorany.ichatu.exception;

public class NoIdentityException extends RuntimeException{

    public NoIdentityException() {
        this("식별자가 없습니다.");
    }

    public NoIdentityException(String message) {
    }

    public NoIdentityException(String message, Throwable cause) {
    }

    public NoIdentityException(Throwable cause) {
    }

    protected NoIdentityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    }
}
