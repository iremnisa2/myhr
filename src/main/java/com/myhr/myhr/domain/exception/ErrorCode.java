package com.myhr.myhr.domain.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "Company not found"),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "Already exists"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Approval token not found"),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Approval token expired"),
    TOKEN_USED(HttpStatus.BAD_REQUEST, "Approval token already used"),
    PASSWORD_TOO_WEAK(HttpStatus.BAD_REQUEST, "Password does not meet requirements"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,  "Email already exists"),
    PHONE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,   "Phone number already exists"),
    MAIL_NOT_SEND(HttpStatus.BAD_REQUEST,   "Mail not send"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
    public final HttpStatus status;
    public final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
