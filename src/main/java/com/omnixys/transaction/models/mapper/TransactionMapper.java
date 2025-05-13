package com.omnixys.transaction.models.mapper;

import com.omnixys.transaction.models.dto.TransactionDTO;
import com.omnixys.transaction.models.entities.Transaction;
import com.omnixys.transaction.models.events.TransactionEventDTO;
import com.omnixys.transaction.models.input.CreateTransactionInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
  Transaction toTransaction(CreateTransactionInput createTransactionInput);

  Transaction toTransaction(TransactionEventDTO transactionEventDTO);

  TransactionDTO toDTO(CreateTransactionInput transaction);
}
