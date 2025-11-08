package org.banking.account.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.banking.account.configuration.FeignConfiguration;
import org.banking.account.model.dto.external.TransactionResponse;

import java.util.List;

@FeignClient(name = "transaction-service", configuration = FeignConfiguration.class)
public interface TransactionService {

    /**
     * Retrieves a list of transactions from the specified account ID.
     *
     * @param accountId the ID of the account
     * @return a list of transaction responses
     */
    @GetMapping("/transactions")
    List<TransactionResponse> getTransactionsFromAccountId(@RequestParam String accountId);
}