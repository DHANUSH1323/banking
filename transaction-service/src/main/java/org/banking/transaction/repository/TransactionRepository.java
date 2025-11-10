package org.banking.transaction.repository;

import java.util.List;

import org.banking.transaction.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTransactionByAccountId(String accountId);

    List<Transaction> findTransactionByReferenceId(String referenceId);
}
