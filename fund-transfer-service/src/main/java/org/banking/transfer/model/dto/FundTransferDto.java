package org.banking.transfer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banking.transfer.model.TransactionStatus;
import org.banking.transfer.model.TransferType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FundTransferDto {

    private String transactionReference;

    private String fromAccount;

    private String toAccount;

    private BigDecimal amount;

    private TransactionStatus status;

    private TransferType transferType;

    private LocalDateTime transferredOn;
}