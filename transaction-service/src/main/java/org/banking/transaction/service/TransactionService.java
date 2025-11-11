package org.banking.transaction.service;

import java.util.List;

import org.banking.transaction.model.dto.TransactionDto;
import org.banking.transaction.model.response.Response;
import org.banking.transaction.model.response.TransactionRequest;

public interface TransactionService {

    Response addTransaction(TransactionDto transactionDto);

    Response internalTransaction(List<TransactionDto> transactionDtos, String transactionReference);

    List<TransactionRequest> getTransaction(String accountId);

    List<TransactionRequest> getTransactionByTransactionReference(String transactionReference);
}
