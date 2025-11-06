package org.banking.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.banking.account.model.AccountType;
import org.banking.account.model.entity.Account;

import java.util.Optional;
public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findAccountByUserIdAndAccountType(Long userId, AccountType accounttype);

    Optional<Account> findAccountByAccountNumber(String accountNumber);

    Optional<Account> findAccountByUserId(Long userId);
    
}
