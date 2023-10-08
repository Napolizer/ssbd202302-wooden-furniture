package pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;

public class AccountFacadeExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext ictx) throws Exception {
    try {
      return ictx.proceed();
    } catch (OptimisticLockException ole) {
      throw ole;
    } catch (PersistenceException pe) {
      String msg = pe.getMessage();
      if (msg.contains("account_login_key") || msg.contains("account.LOGIN")) {
        throw ApplicationExceptionFactory.createLoginAlreadyExistsException(pe);
      } else if (msg.contains("account_email_key") || msg.contains("account.EMAIL")) {
        throw ApplicationExceptionFactory.createEmailAlreadyExistsException(pe);
      } else if (msg.contains("company_nip_key") || msg.contains("company.NIP")) {
        throw ApplicationExceptionFactory.createNipAlreadyExistsException(pe);
      } else {
        throw pe;
      }
    }
  }
}
