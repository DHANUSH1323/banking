package org.banking.transfer.service;

import org.banking.transfer.model.dto.FundTransferDto;
import org.banking.transfer.model.dto.request.FundTransferRequest;
import org.banking.transfer.model.dto.response.FundTransferResponse;

import java.util.List;

public interface FundTransferService {

    
    FundTransferResponse fundTransfer(FundTransferRequest fundTransferRequest);

    FundTransferDto getTransferDetailsFromReferenceId(String referenceId);

    List<FundTransferDto> getAllTransfersByAccountId(String accountId);
}