package org.banking.account.exception;

public class InSufficientFunds extends GlobalException{
    public InSufficientFunds(){
        super("Insufficient Funds", GlobalErrorCode.Not_Found);
    }

    public InSufficientFunds(String message){
        super(message, GlobalErrorCode.Not_Found);
    }
    
}
