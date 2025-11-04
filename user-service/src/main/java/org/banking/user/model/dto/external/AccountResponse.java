package org.banking.user.model.dto.external;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private String accountNumber;

    private BigDecimal actualBalance;

    private Integer id;

    private String type;

    private String status;

    private BigDecimal availableBalance;
}
