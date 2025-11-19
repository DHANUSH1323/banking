package org.banking.transaction.service.implementation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.banking.transaction.exception.AccountStatusException;
import org.banking.transaction.exception.GlobalErrorCode;
import org.banking.transaction.exception.InsufficientBalance;
import org.banking.transaction.exception.ResourceNotFound;
import org.banking.transaction.external.AccountService;
import org.banking.transaction.model.TransactionStatus;
import org.banking.transaction.model.TransactionType;
import org.banking.transaction.model.dto.TransactionDto;
import org.banking.transaction.model.entity.Transaction;
import org.banking.transaction.model.external.Account;
import org.banking.transaction.model.mapper.TransactionMapper;
import org.banking.transaction.model.response.Response;
import org.banking.transaction.model.response.TransactionRequest;
import org.banking.transaction.repository.TransactionRepository;
import org.banking.transaction.service.TransactionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    private final TransactionMapper transactionMapper = new TransactionMapper();

    @Value("${spring.application.ok}")
    private String ok;

    @Override
    @Caching(evict = {
        @CacheEvict(value = "transactionsByAccount", key = "#transactionDto.accountId"),
        @CacheEvict(value = "transactionsByReference", allEntries = true)
    })
    public Response addTransaction(TransactionDto transactionDto) {

        ResponseEntity<Account> response = getAccountWithResilience(transactionDto.getAccountId());
        if (Objects.isNull(response.getBody())) {
            throw new ResourceNotFound("Requested account not found on the server", GlobalErrorCode.NOT_FOUND);
        }

        Account account = response.getBody();
        Transaction transaction = transactionMapper.convertToEntity(transactionDto);

        if (transactionDto.getTransactionType().equals(TransactionType.DEPOSIT.toString())) {

            account.setAvailableBalance(account.getAvailableBalance().add(transactionDto.getAmount()));

        } else if (transactionDto.getTransactionType().equals(TransactionType.WITHDRAWAL.toString())) {

            if (!account.getAccountStatus().equals("ACTIVE")) {
                throw new AccountStatusException("account is inactive or closed");
            }

            if (account.getAvailableBalance().compareTo(transactionDto.getAmount()) < 0) {
                throw new InsufficientBalance("Insufficient balance in the account");
            }

            transaction.setAmount(transactionDto.getAmount().negate());
            account.setAvailableBalance(account.getAvailableBalance().subtract(transactionDto.getAmount()));
        }

        transaction.setTransactionType(TransactionType.valueOf(transactionDto.getTransactionType()));
        transaction.setComments(transactionDto.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setReferenceId(UUID.randomUUID().toString());

        updateAccountWithResilience(transactionDto.getAccountId(), account);

        transactionRepository.save(transaction);

        return Response.builder()
                .message("Transaction completed successfully")
                .responseCode(ok)
                .build();
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "transactionsByAccount", allEntries = true),
        @CacheEvict(value = "transactionsByReference", allEntries = true)
    })
    public Response internalTransaction(List<TransactionDto> transactionDtos, String transactionReference) {

        List<Transaction> transactions = transactionMapper.convertToEntityList(transactionDtos);

        transactions.forEach(transaction -> {
            transaction.setTransactionType(TransactionType.INTERNAL_TRANSFER);
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setReferenceId(transactionReference);
        });

        transactionRepository.saveAll(transactions);

        return Response.builder()
                .responseCode(ok)
                .message("Transaction completed successfully").build();
    }

    @Override
    @Cacheable(value = "transactionsByAccount", key = "#accountId")
    public List<TransactionRequest> getTransaction(String accountId) {

        return transactionRepository.findTransactionByAccountId(accountId)
                .stream().map(transaction -> {
                    TransactionRequest transactionRequest = new TransactionRequest();
                    BeanUtils.copyProperties(transaction, transactionRequest);
                    transactionRequest.setTransactionStatus(transaction.getStatus().toString());
                    transactionRequest.setLocalDateTime(transaction.getTransactionDate());
                    transactionRequest.setTransactionType(transaction.getTransactionType().toString());
                    return transactionRequest;
                }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "transactionsByReference", key = "#transactionReference")
    public List<TransactionRequest> getTransactionByTransactionReference(String transactionReference) {

        return transactionRepository.findTransactionByReferenceId(transactionReference)
                .stream().map(transaction -> {
                    TransactionRequest transactionRequest = new TransactionRequest();
                    BeanUtils.copyProperties(transaction, transactionRequest);
                    transactionRequest.setTransactionStatus(transaction.getStatus().toString());
                    transactionRequest.setLocalDateTime(transaction.getTransactionDate());
                    transactionRequest.setTransactionType(transaction.getTransactionType().toString());
                    return transactionRequest;
                }).collect(Collectors.toList());
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "accountServiceReadFallback")
    @Retry(name = "accountService")
    @RateLimiter(name = "accountService")
    public ResponseEntity<Account> getAccountWithResilience(String accountId) {
        return accountService.readByAccountNumber(accountId);
    }

    public ResponseEntity<Account> accountServiceReadFallback(String accountId, Throwable ex) {
        log.error("Fallback triggered for readByAccountNumber. AccountId = {}, Error = {}", accountId, ex.toString());
        return ResponseEntity.status(503).body(null);
    }


    @CircuitBreaker(name = "accountService", fallbackMethod = "accountServiceUpdateFallback")
    @Retry(name = "accountService")
    @RateLimiter(name = "accountService")
    public void updateAccountWithResilience(String accountId, Account account) {
        accountService.updateAccount(accountId, account);
    }

    public void accountServiceUpdateFallback(String accountId, Account account, Throwable ex) {
        log.error("Fallback triggered for updateAccount. AccountId = {}, Error = {}", accountId, ex.toString());
        throw new RuntimeException("Account service unavailable. Cannot update account.");
    }
}
