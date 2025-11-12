package org.banking.transfer.model.mapper;

import org.springframework.beans.BeanUtils;
import org.banking.transfer.model.dto.FundTransferDto;
import org.banking.transfer.model.entity.FundTransfer;

import java.util.Objects;

public class FundTransferMapper extends BaseMapper<FundTransfer, FundTransferDto> {

    @Override
    public FundTransfer convertToEntity(FundTransferDto dto, Object... args) {

        FundTransfer fundTransfer = new FundTransfer();
        if(!Objects.isNull(dto)){
            BeanUtils.copyProperties(dto, fundTransfer);
        }
        return fundTransfer;
    }

    @Override
    public FundTransferDto convertToDto(FundTransfer entity, Object... args) {

        FundTransferDto fundTransferDto = new FundTransferDto();
        if(!Objects.isNull(entity)){
            BeanUtils.copyProperties(entity, fundTransferDto);
        }
        return fundTransferDto;
    }
}