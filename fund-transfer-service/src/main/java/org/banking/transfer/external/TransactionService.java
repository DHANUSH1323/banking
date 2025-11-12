package org.banking.transfer.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.banking.transfer.configuration.FeignClientConfiguration;
import org.banking.transfer.model.dto.Transaction;
import org.banking.transfer.model.dto.response.Response;

import java.util.List;

@FeignClient(name = "transaction-service", configuration = FeignClientConfiguration.class)
public interface TransactionService {

    @PostMapping("/transactions")
    ResponseEntity<Response> makeTransaction(@RequestBody Transaction transaction);

    @PostMapping("/transactions/internal")
    ResponseEntity<Response> makeInternalTransactions(@RequestBody List<Transaction> transactions,@RequestParam String transactionReference);
}