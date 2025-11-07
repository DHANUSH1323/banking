package org.banking.account.exception;

public class ResourceConflict extends GlobalException{
    public ResourceConflict(){
        super("Account already Exists", GlobalErrorCode.CONFLICT);
    }

    public ResourceConflict(String message){
        super(message, GlobalErrorCode.CONFLICT);
    }
    
}
