package org.banking.transfer.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.banking.transfer.exception.AccountUpdateException;
import org.banking.transfer.exception.GlobalErrorCode;
import org.banking.transfer.exception.InsufficientBalance;
import org.banking.transfer.exception.ResourceNotFound;
import org.banking.transfer.external.AccountService;
import org.banking.transfer.external.TransactionService;
import org.banking.transfer.model.mapper.FundTransferMapper;
import org.banking.transfer.model.TransactionStatus;
import org.banking.transfer.model.TransferType;
import org.banking.transfer.model.dto.Account;
import org.banking.transfer.model.dto.FundTransferDto;
import org.banking.transfer.model.dto.Transaction;
import org.banking.transfer.model.dto.request.FundTransferRequest;
import org.banking.transfer.model.dto.response.FundTransferResponse;
import org.banking.transfer.model.entity.FundTransfer;
import org.banking.transfer.repository.FundTransferRepository;
import org.banking.transfer.service.FundTransferService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundTransferServiceImpl implements FundTransferService {

    private final AccountService accountService;
    private final FundTransferRepository fundTransferRepository;
    private final TransactionService transactionService;

    @Value("${spring.application.ok}")
    private String ok;

    private final FundTransferMapper fundTransferMapper = new FundTransferMapper();

    @Override
    public FundTransferResponse fundTransfer(FundTransferRequest fundTransferRequest) {

        Account fromAccount;
        ResponseEntity<Account> response = accountService.readByAccountNumber(fundTransferRequest.getFromAccount());
        if(Objects.isNull(response.getBody())){
            log.error("requested account "+fundTransferRequest.getFromAccount()+" is not found on the server");
            throw new ResourceNotFound("requested account not found on the server", GlobalErrorCode.NOT_FOUND);
        }
        fromAccount = response.getBody();
        if (!fromAccount.getAccountStatus().equals("ACTIVE")) {
            log.error("account status is pending or inactive, please update the account status");
            throw new AccountUpdateException("account is status is :pending", GlobalErrorCode.NOT_ACCEPTABLE);
        }
        if (fromAccount.getAvailableBalance().compareTo(fundTransferRequest.getAmount()) < 0) {
            log.error("required amount to transfer is not available");
            throw new InsufficientBalance("requested amount is not available", GlobalErrorCode.NOT_ACCEPTABLE);
        }
        Account toAccount;
        response = accountService.readByAccountNumber(fundTransferRequest.getToAccount());
        if(Objects.isNull(response.getBody())) {
            log.error("requested account "+fundTransferRequest.getToAccount()+" is not found on the server");
            throw new ResourceNotFound("requested account not found on the server", GlobalErrorCode.NOT_FOUND);
        }
        toAccount = response.getBody();
        String transactionId = internalTransfer(fromAccount, toAccount, fundTransferRequest.getAmount());
        FundTransfer fundTransfer = FundTransfer.builder()
                .transferType(TransferType.INTERNAL)
                .amount(fundTransferRequest.getAmount())
                .fromAccount(fromAccount.getAccountNumber())
                .transactionReference(transactionId)
                .status(TransactionStatus.SUCCESS)
                .toAccount(toAccount.getAccountNumber()).build();

        fundTransferRepository.save(fundTransfer);
        return FundTransferResponse.builder()
                .transactionId(transactionId)
                .message("Fund transfer was successful").build();
    }

    private String internalTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {

        fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(amount));
        accountService.updateAccount(fromAccount.getAccountNumber(), fromAccount);

        toAccount.setAvailableBalance(toAccount.getAvailableBalance().add(amount));
        accountService.updateAccount(toAccount.getAccountNumber(), toAccount);

        List<Transaction> transactions = List.of(
                Transaction.builder()
                        .accountId(fromAccount.getAccountNumber())
                        .transactionType("INTERNAL_TRANSFER")
                        .amount(amount.negate())
                        .description("Internal fund transfer from "+fromAccount.getAccountNumber()+" to "+toAccount.getAccountNumber())
                        .build(),
                Transaction.builder()
                        .accountId(toAccount.getAccountNumber())
                        .transactionType("INTERNAL_TRANSFER")
                        .amount(amount)
                        .description("Internal fund transfer received from: "+fromAccount.getAccountNumber()).build());

        String transactionReference = UUID.randomUUID().toString();
        transactionService.makeInternalTransactions(transactions, transactionReference);
        return transactionReference;
    }

    @Override
    public FundTransferDto getTransferDetailsFromReferenceId(String referenceId) {

        return fundTransferRepository.findFundTransferByTransactionReference(referenceId)
                .map(fundTransferMapper::convertToDto)
                .orElseThrow(() -> new ResourceNotFound("Fund transfer not found", GlobalErrorCode.NOT_FOUND));
    }

    @Override
    public List<FundTransferDto> getAllTransfersByAccountId(String accountId) {

        return fundTransferMapper.convertToDtoList(fundTransferRepository.findFundTransferByFromAccount(accountId));
    }
}