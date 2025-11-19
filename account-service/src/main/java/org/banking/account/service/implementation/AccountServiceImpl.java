package org.banking.account.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.banking.account.exception.*;
import org.banking.account.external.SequenceService;
import org.banking.account.external.TransactionService;
import org.banking.account.external.UserService;
import org.banking.account.model.AccountStatus;
import org.banking.account.model.AccountType;
import org.banking.account.model.dto.AccountDto;
import org.banking.account.model.dto.AccountStatusUpdate;
import org.banking.account.model.dto.external.UserDto;
import org.banking.account.model.dto.response.Response;
import org.banking.account.model.entity.Account;
import org.banking.account.model.mapper.AccountMapper;
import org.banking.account.model.dto.external.TransactionResponse;
import org.banking.account.repository.AccountRepository;
import org.banking.account.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

import static org.banking.account.model.Constants.ACC_PREFIX;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService{

    private final UserService userService;
    private final SequenceService sequenceService;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper = new AccountMapper();

    @Value("${spring.application.ok}")
    private String success;

    @Override
    public Response createAccount(AccountDto accountDto){

        UserDto user = getUserByIdWithResilience(accountDto.getUserId());
        if (user == null) {
            throw new ResourceNotFound("User not found on server");
        }

        accountRepository.findAccountByUserIdAndAccountType(
                accountDto.getUserId(),
                AccountType.valueOf(accountDto.getAccountType())
        ).ifPresent(account -> {
            throw new ResourceConflict("Account already exist on the server");
        });

        Account account = accountMapper.convertToEntity(accountDto);

        Long nextSeq = getNextSequenceWithResilience(); 
        account.setAccountNumber(
                ACC_PREFIX + String.format("%07d", nextSeq)
        );

        account.setAccountStatus(AccountStatus.PENDING);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setAccountType(AccountType.valueOf(accountDto.getAccountType()));

        accountRepository.save(account);

        return Response.builder()
                .responseCode(success)
                .message("Account created successfully")
                .build();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "userFallback")
    @Retry(name = "userService")
    @RateLimiter(name = "userService")
    public UserDto getUserByIdWithResilience(Long userId) {
        ResponseEntity<UserDto> response = userService.readUserById(userId);
        return response.getBody();
    }

    public UserDto userFallback(Long userId, Throwable ex) {
        log.error("User-service fallback triggered. Reason = {}", ex.toString());
        return null;
    }

    @CircuitBreaker(name = "sequenceService", fallbackMethod = "sequenceFallback")
    @Retry(name = "sequenceService")
    @RateLimiter(name = "sequenceService")
    public Long getNextSequenceWithResilience() {
        return sequenceService.generateAccountNumber().getAccountNumber();
    }

    public Long sequenceFallback(Throwable ex) {
        log.error("Sequence-service fallback triggered. Reason = {}", ex.toString());
        throw new RuntimeException("Sequence Generator unavailable. Cannot create account at the moment.");
    }

    
    @Override
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public Response updateStatus(String accountNumber, AccountStatusUpdate accountUpdate){
        return accountRepository.findAccountByAccountNumber(accountNumber)
                .map(account -> {
                    if(account.getAccountStatus().equals(AccountStatus.ACTIVE)){
                        throw new AccountStatusException("Account is inactive/closed");
                    }
                    if(account.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0 || account.getAvailableBalance().compareTo(BigDecimal.valueOf(1000)) < 0){
                        throw new InSufficientFunds("Minimum balance of Rs.1000 is required");
                    }
                    account.setAccountStatus(accountUpdate.getAccountStatus());
                    accountRepository.save(account);
                    return Response.builder().message("Account updated successfully").responseCode(success).build();
                }).orElseThrow(() -> new ResourceNotFound("Account not on the server"));
    }

    @Override
    @Cacheable(value = "accounts", key = "#accountNumber", unless = "#result == null or #result.accountStatus.equals('CLOSED')")
    public AccountDto readAccountByAccountNumber(String accountNumber) {

        return accountRepository.findAccountByAccountNumber(accountNumber)
                .map(account -> {
                    AccountDto accountDto = accountMapper.convertToDto(account);
                    accountDto.setAccountType(account.getAccountType().toString());
                    accountDto.setAccountStatus(account.getAccountStatus().toString());
                    return accountDto;
                })
                .orElseThrow(ResourceNotFound::new);
    }

    @Override
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public Response updateAccount(String accountNumber, AccountDto accountDto) {

        return accountRepository.findAccountByAccountNumber(accountDto.getAccountNumber())
                .map(account -> {
                    BeanUtils.copyProperties(accountDto, account);
                    accountRepository.save(account);
                    return Response.builder()
                            .responseCode(success)
                            .message("Account updated successfully").build();
                }).orElseThrow(() -> new ResourceNotFound("Account not found on the server"));
    }

    @Override
    public String getBalance(String accountNumber) {

        return accountRepository.findAccountByAccountNumber(accountNumber)
                .map(account -> account.getAvailableBalance().toString())
                .orElseThrow(ResourceNotFound::new);
    }

    @Override
    @CircuitBreaker(name = "transactionService", fallbackMethod = "transactionFallback")
    @Retry(name = "transactionService")
    @RateLimiter(name = "transactionService")
    public List<TransactionResponse> getTransactionsFromAccountId(String accountId) {

        return transactionService.getTransactionsFromAccountId(accountId);
    }

    @Override
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public Response closeAccount(String accountNumber) {

        return accountRepository.findAccountByAccountNumber(accountNumber)
                .map(account -> {
                    if(BigDecimal.valueOf(Double.parseDouble(getBalance(accountNumber))).compareTo(BigDecimal.ZERO) != 0) {
                        throw new AccountClosingException("Balance should be zero");
                    }
                    account.setAccountStatus(AccountStatus.CLOSED);
                    return Response.builder()
                            .message("Account closed successfully").message(success)
                            .build();
                }).orElseThrow(ResourceNotFound::new);

    }

    @Override
    public AccountDto readAccountByUserId(Long userId) {

        return accountRepository.findAccountByUserId(userId)
                .map(account ->{
                    if(!account.getAccountStatus().equals(AccountStatus.ACTIVE)){
                        throw new AccountStatusException("Account is inactive/closed");
                    }
                    AccountDto accountDto = accountMapper.convertToDto(account);
                    accountDto.setAccountStatus(account.getAccountStatus().toString());
                    accountDto.setAccountType(account.getAccountType().toString());
                    return accountDto;
                }).orElseThrow(ResourceNotFound::new);
    }
    
}
