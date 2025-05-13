package com.omnixys.transaction.models.entities;

import com.omnixys.transaction.models.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Transaction {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private UUID id;

  @Transient
  private TransactionType type;

//  @Enumerated(EnumType.STRING)
//  private CurrencyType currencyType;
  private BigDecimal amount;

  private UUID sender;
  private UUID receiver;

  @CreationTimestamp
  private LocalDateTime created;
}
