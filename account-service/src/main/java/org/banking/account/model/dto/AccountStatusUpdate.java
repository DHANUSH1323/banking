package org.banking.account.model.dto;

import lombok.Data;
import org.banking.account.model.AccountStatus;

@Data
public class AccountStatusUpdate {
    AccountStatus accountStatus;
    
}
