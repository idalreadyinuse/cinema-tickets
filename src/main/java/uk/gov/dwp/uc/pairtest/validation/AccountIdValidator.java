package uk.gov.dwp.uc.pairtest.validation;

import java.util.logging.Logger;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class AccountIdValidator {

  private static final Logger LOG = Logger.getLogger(AccountIdValidator.class.getName());

  public void validate(Long id) throws InvalidPurchaseException {
    if (id == null || id <= 0L) {
      LOG.info("Account ID is invalid");
      throw new InvalidPurchaseException("Account ID is invalid");
    }
  }
}
