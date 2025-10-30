package com.vendeskgt.vendeskgt.controllerAdvice;

import com.google.firebase.ErrorCode;
import lombok.Getter;

@Getter
public class FirebaseCustomException extends RuntimeException {
    private final ErrorCode code;
    private final String description;

    public FirebaseCustomException(ErrorCode code, String description) {
        super(description);
        this.code = code;
        this.description = description;
    }

}

