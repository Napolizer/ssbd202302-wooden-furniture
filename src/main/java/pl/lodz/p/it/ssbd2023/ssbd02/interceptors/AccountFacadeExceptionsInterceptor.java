package pl.lodz.p.it.ssbd2023.ssbd02.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import java.util.ArrayList;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;

public class AccountFacadeExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext ictx) throws Exception {
    try {
      return ictx.proceed();
    } catch (OptimisticLockException ole) {
      throw ole;
    } catch (PersistenceException pe) {
      var causes = new ArrayList<Throwable>();
      for (Throwable t = pe; t != null; t = t.getCause()) {
        causes.add(t);
      }
      for (var cause : causes) {
        if (cause.getMessage().contains("account_login_key")) {
          throw ApplicationExceptionFactory.createLoginAlreadyExistsException(pe);
        } else if (cause.getMessage().contains("account_email_key")) {
          throw ApplicationExceptionFactory.createEmailAlreadyExistsException(pe);
        } else if (cause.getMessage().contains("company_nip_key")) {
          throw ApplicationExceptionFactory.createNipAlreadyExistsException(pe);
        }
      }
      throw pe;
    }
  }
}
