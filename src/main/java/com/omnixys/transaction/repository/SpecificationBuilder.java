package com.omnixys.transaction.repository;

import com.omnixys.transaction.models.entities.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class SpecificationBuilder {
  public Optional<Specification<Transaction>> build(final Map<String, ? extends List<Object>> queryParams) {
    log.debug("build: queryParams={}", queryParams);

    if (queryParams.isEmpty()) {
      // No search criteria provided
      return Optional.empty();
    }

    final var specs = queryParams
      .entrySet()
      .stream()
      .map(this::toSpecification)
      .toList();

    if (specs.isEmpty() || specs.contains(null)) {
      return Optional.empty();
    }

    return Optional.of(Specification.allOf(specs));
  }

  private Specification<Transaction> toSpecification(final Map.Entry<String, ? extends List<Object>> entry) {
    log.trace("toSpec: entry={}", entry);
    final var key = entry.getKey();
    final var values = entry.getValue();

    if (values == null || values.size() != 1) {
      return null;
    }

    final var value = values.getFirst();
    return switch (key) {
//      case "type" -> transactionType(value);
      case "sender" -> sender(value.toString());
      case "receiver" -> receiver(value.toString());
      default -> throw new IllegalArgumentException(key);
    };
  }


//  private Specification<Transaction> transactionType(final String value) {
//    return (root, query, builder) -> builder.equal(
//      root.get(Transaction_.type),
//      TransactionType.of(value)
//    );
//  }

  private Specification<Transaction> sender(final String value) {
    try {
      UUID senderId = UUID.fromString(value);
      return (root, _, builder) -> builder.equal(root.get("sender"), senderId);
    } catch (IllegalArgumentException e) {
      log.error("Invalid UUID format for sender: {}", value);
      return (_, _, builder) -> builder.conjunction(); // Gibt immer true zurück, d.h. kein Filter
    }
  }

  private Specification<Transaction> receiver(final String value) {
    try {
      UUID receiverId = UUID.fromString(value);
      return (root, _, builder) -> builder.equal(root.get("receiver"), receiverId);
    } catch (IllegalArgumentException e) {
      log.error("Invalid UUID format for receiver: {}", value);
      return (_, _, builder) -> builder.conjunction(); // Gibt immer true zurück, d.h. kein Filter
    }
  }
}
