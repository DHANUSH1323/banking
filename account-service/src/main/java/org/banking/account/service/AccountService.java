package org.banking.account.service;

import org.banking.account.model.dto.external.TransactionResponse;
import org.banking.account.model.dto.response.Response;
import org.banking.account.model.dto.AccountDto;
import org.banking.account.model.dto.AccountStatusUpdate;

import java.util.List;

public interface AccountService {

    Response createAccount(AccountDto accountDto);

    Response updateStatus(String accountNumber, AccountStatusUpdate accountUpdate);

    AccountDto readAccountByAccountNumber(String accountNumber);

    Response updateAccount(String accountNumber, AccountDto accountDto);

    String getBalance(String accountNumber);

    List<TransactionResponse> getTransactionsFromAccountId(String accountId);

    Response closeAccount(String accountNumber);

    AccountDto readAccountByUserId(Long userId);
}
