package org.banking.transfer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.banking.transfer.model.entity.FundTransfer;

import java.util.List;
import java.util.Optional;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {

    Optional<FundTransfer> findFundTransferByTransactionReference(String referenceId);

    List<FundTransfer> findFundTransferByFromAccount(String accountId);
}