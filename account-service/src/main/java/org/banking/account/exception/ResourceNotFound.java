package org.banking.account.exception;

public class ResourceNotFound extends GlobalException{

    public ResourceNotFound(){
        super("Resource not found on server", GlobalErrorCode.Not_Found);
    }

    public ResourceNotFound(String message){
        super(message, GlobalErrorCode.Not_Found);
    }
    
}
