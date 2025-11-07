package org.banking.account.exception;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

@RequiredArgsConstructor
@Getter
public class GlobalException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;

    // public String getErrorCode() {
    //     return errorCode;
    // }

    // public String getErrorMessage() {
    //     return errorMessage;
    // }
    
}
