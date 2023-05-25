package pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.endpoint;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.ws.rs.WebApplicationException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.TokenService;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;

public abstract class AbstractEndpoint {

  private static final Logger logger = Logger.getLogger(LoggerInterceptor.class.getName());
  private Integer retryCounterValue;

  @PostConstruct
  public void init() {
    Properties prop = new Properties();
    try (InputStream input = TokenService.class.getClassLoader().getResourceAsStream("config.properties")) {
      prop.load(input);
      retryCounterValue = Integer.parseInt(prop.getProperty("transaction.retries"));
    } catch (Exception e) {
      retryCounterValue = 3;
    }
  }

  protected <T> T repeatTransaction(TransactionMethod<T> method) {
    int retryCounter = retryCounterValue;
    boolean isRollback = true;
    T result = null;

    while (isRollback) {
      try {
        logger.log(Level.INFO, "Transaction number " + (retryCounterValue - retryCounter + 1));
        result = method.run();
        isRollback = isLastTransactionRollback();

      } catch (BaseWebApplicationException | EJBTransactionRolledbackException ex) {
        logger.log(Level.WARNING, "Transaction number "
                + (retryCounterValue - retryCounter + 1) + " failed");
        retryCounter--;

        if (retryCounter == 0) {
          throw ex;
        }
      }
    }

    return result;
  }

  protected void repeatTransaction(VoidTransactionMethod method) {
    int retryCounter = retryCounterValue;
    boolean isRollback = true;

    while (isRollback) {
      try {
        logger.log(Level.INFO, "Transaction number " + (retryCounterValue - retryCounter + 1));
        method.run();
        isRollback = isLastTransactionRollback();

      } catch (WebApplicationException | EJBTransactionRolledbackException ex) {
        logger.log(Level.WARNING, "Transaction number "
                + (retryCounterValue - retryCounter + 1) + " failed");
        retryCounter--;

        if (retryCounter == 0) {
          throw ex;
        }
      }
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
