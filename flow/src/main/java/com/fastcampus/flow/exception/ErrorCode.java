package com.fastcampus.flow.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
public enum ErrorCode {
    QUEUE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "UQ-0001", "Already Registered in queue"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UQ-1001", "error: %s"),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;

    public ApplicationException build() {
        return new ApplicationException(httpStatus, code, reason);
    }

    public ApplicationException build(Object ...args) {
        return new ApplicationException(httpStatus, code, reason.formatted(args));
    }
}
