package org.banking.account.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.banking.account.model.dto.external.SequenceDto;

@FeignClient(name = "sequence-generator")
public interface SequenceService {

    /**
     * Generates a new account number.
     *
     * @return the generated account number as a SequenceDto object.
     */
    @PostMapping("/sequence")
    SequenceDto generateAccountNumber();
}