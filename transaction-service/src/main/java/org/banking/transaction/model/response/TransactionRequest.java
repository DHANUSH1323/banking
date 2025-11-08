package org.banking.transaction.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    private String referenceId;

    private String accountId;

    private String transactionType;

    private BigDecimal amount;

    private LocalDateTime localDateTime;

    private String transactionStatus;

    private String comments;

}
