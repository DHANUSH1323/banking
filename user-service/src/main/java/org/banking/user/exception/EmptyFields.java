package org.banking.user.exception;

public class EmptyFields extends GlobalException{
    public EmptyFields(String message, String errorCode){
        super(message, errorCode);
    }
    
}
