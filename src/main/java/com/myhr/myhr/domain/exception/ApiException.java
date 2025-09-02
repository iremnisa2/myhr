package com.myhr.myhr.domain.exception;

public class ApiException extends RuntimeException {
    private final ErrorCode code;

    public ApiException(ErrorCode code) {
        super(code.message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }

    public ApiException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }


    public ApiException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
