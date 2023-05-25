package pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.ejb.SessionContext;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import java.rmi.RemoteException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractService implements SessionSynchronization {
  @Resource
  private SessionContext sctx;
  protected static final Logger LOGGER = Logger.getGlobal();

  private String transactionId;
  private boolean lastTransactionRollback;

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  public boolean isLastTransactionRollback() {
    return lastTransactionRollback;
  }

  @Override
  public void afterBegin() throws EJBException, RemoteException {
    transactionId = Long.toString(System.currentTimeMillis())
            + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    LOGGER.log(Level.INFO, "Transaction TXid={0} started in {1}, identity: {2}",
            new Object[] {
              transactionId, this.getClass().getName(),
              sctx.getCallerPrincipal().getName()
            }
    );
  }

  @Override
  public void beforeCompletion() throws EJBException, RemoteException {
    LOGGER.log(Level.INFO, "Transaction TXid={0} before commit in {1}, identity: {2}",
            new Object[] {
              transactionId, this.getClass().getName(),
              sctx.getCallerPrincipal().getName()
            }
    );
  }

  @Override
  public void afterCompletion(boolean committed) throws EJBException, RemoteException {
    lastTransactionRollback = !committed;
    LOGGER.log(Level.INFO, "Transaction TXid={0} ends in {1}" + " with {3}, identity {2}",
            new Object[] {
              transactionId, this.getClass().getName(),
              sctx.getCallerPrincipal().getName(),
              committed ? "COMMIT" : "ROLLBACK"
            }
    );
  }
}
