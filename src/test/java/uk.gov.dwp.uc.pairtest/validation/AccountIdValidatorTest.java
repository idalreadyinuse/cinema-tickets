package uk.gov.dwp.uc.pairtest.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class AccountIdValidatorTest {

  private static AccountIdValidator accountIdValidator;

  @BeforeClass
  public static void setUp() {
    accountIdValidator = new AccountIdValidator();
  }

  @Test
  public void account_id_zero_should_throw_exception() {

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
    () -> accountIdValidator.validate(0L));

    assertEquals("Account ID is invalid", thrown.getMessage());
  }

  @Test
  public void account_id_null_should_throw_exception() {

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> accountIdValidator.validate(null));

    assertEquals("Account ID is invalid", thrown.getMessage());
  }

  @Test
  public void account_id_less_than_zero_should_throw_exception() {

    InvalidPurchaseException thrown =
        assertThrows(InvalidPurchaseException.class,
            () -> accountIdValidator.validate(-2L));

    assertEquals("Account ID is invalid", thrown.getMessage());
  }
}
