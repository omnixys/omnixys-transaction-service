package com.gentlecorp.transaction.util;

import com.gentlecorp.transaction.exceptions.ConstraintViolationsException;
import com.gentlecorp.transaction.exceptions.VersionAheadException;
import com.gentlecorp.transaction.exceptions.VersionInvalidException;
import com.gentlecorp.transaction.exceptions.VersionOutdatedException;
import com.gentlecorp.transaction.messaging.KafkaPublisherService;
import com.gentlecorp.transaction.models.entities.Transaction;
import com.gentlecorp.transaction.tracing.LoggerPlus;
import com.gentlecorp.transaction.tracing.LoggerPlusFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.gentlecorp.transaction.util.Constants.VERSION_NUMBER_MISSING;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;

@Service
@RequiredArgsConstructor
public class ValidationService {

  private final Validator validator;
  private final KafkaPublisherService kafkaPublisherService;
  private final LoggerPlusFactory factory;
  private LoggerPlus logger() {
    return factory.getLogger(getClass());
  }

//  public <T> void validateDTO(T dto, Class<?>... groups) {
//    // Standard-Validierung ausführen
//    final Set<ConstraintViolation<T>> violations = validator.validate(dto, groups);
//
//    // 🔥 Hier wird auch klassenbezogene Validierung berücksichtigt!
//    final Set<ConstraintViolation<T>> classLevelViolations = validator.validate(dto);
//
//    // Beide Validierungen zusammenführen
//    violations.addAll(classLevelViolations);
//
//    if (!violations.isEmpty()) {
//      logger().debug("🚨 Validation failed: {}", violations);
//
//      if (dto instanceof CreateCustomerInput) {
//        @SuppressWarnings("unchecked")
//        var customerViolations = new ArrayList<>((Collection<ConstraintViolation<CreateCustomerInput>>) (Collection<?>) violations);
//        throw new ConstraintViolationsException(customerViolations, null);
//      }
//
//      if (dto instanceof ContactDTO) {
//        @SuppressWarnings("unchecked")
//        var contactViolations = new ArrayList<>((Collection<ConstraintViolation<ContactDTO>>) (Collection<?>) violations);
//        throw new ConstraintViolationsException(null, contactViolations);
//      }
//    }
//  }


  
  public int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
    logger().trace("getVersion: {}", versionOpt);
    return versionOpt.map(versionStr -> {
      if (isValidVersion(versionStr)) {
        return Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
      } else {
        throw new VersionInvalidException(
          PRECONDITION_FAILED,
          String.format("Invalid ETag %s", versionStr), // Korrektur der String-Interpolation
          URI.create(request.getRequestURL().toString())
        );
      }
    }).orElseThrow(() -> new VersionInvalidException(
      PRECONDITION_REQUIRED,
      VERSION_NUMBER_MISSING,
      URI.create(request.getRequestURL().toString())
    ));
  }

  private boolean isValidVersion(String versionStr) {
    logger().debug("length of versionString={} versionString={}", versionStr.length(), versionStr);
    return versionStr.length() >= 3 &&
      versionStr.charAt(0) == '"' &&
      versionStr.charAt(versionStr.length() - 1) == '"';
  }
}
