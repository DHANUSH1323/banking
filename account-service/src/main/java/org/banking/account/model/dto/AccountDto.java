package org.banking.account.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long accountId;
    private String accountNumber;
    private String accountStatus;
    private String accountType;
    private BigDecimal accountBalance;
    private Long userId;
    
}
