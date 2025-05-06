package com.gentlecorp.transaction.models.mapper;

import com.gentlecorp.transaction.models.dto.TransactionDTO;
import com.gentlecorp.transaction.models.entities.Transaction;
import com.gentlecorp.transaction.models.input.CreateTransactionInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
  Transaction toTransaction(CreateTransactionInput createTransactionInput);

  TransactionDTO toDTO(CreateTransactionInput transaction);
}
