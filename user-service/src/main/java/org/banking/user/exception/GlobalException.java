package org.banking.user.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {
    private String message;

    private String errorCode;

    public GlobalException(String message){
        this.message = message;
    }
}
