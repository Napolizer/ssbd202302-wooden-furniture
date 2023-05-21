package pl.lodz.p.it.ssbd2023.ssbd02.sharedmod.endpoint;

import jakarta.ejb.EJBTransactionRolledbackException;

public abstract class AbstractEndpoint {
  protected <T> T repeatTransaction(TransactionMethod<T> method) {
    int retryCounter = 3;
    boolean isRollback;
    T result = null;

    do {
      try {
        result = method.run();
        isRollback = isLastTransactionRollback();
      } catch (EJBTransactionRolledbackException ex) {
        isRollback = true;
      }

    } while (isRollback && --retryCounter > 0);

    if (isRollback) {
      throw new EJBTransactionRolledbackException();
    } else {
      return result;
    }
  }

  protected void repeatTransaction(VoidTransactionMethod method) {
    int retryCounter = 3;
    boolean isRollback;

    do {
      try {
        method.run();
        isRollback = isLastTransactionRollback();
      } catch (EJBTransactionRolledbackException ex) {
        isRollback = true;
      }

    } while (isRollback && --retryCounter > 0);

    if (isRollback) {
      throw new EJBTransactionRolledbackException();
    }
  }

  protected interface TransactionMethod<T> {
    T run();
  }

  protected interface VoidTransactionMethod {
    void run();
  }

  protected abstract boolean isLastTransactionRollback();
}
