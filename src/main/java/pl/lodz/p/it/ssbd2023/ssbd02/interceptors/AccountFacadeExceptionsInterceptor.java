package pl.lodz.p.it.ssbd2023.ssbd02.interceptors;

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
            if (pe.getMessage().contains("account_login_key")) {
                throw ApplicationExceptionFactory.createLoginAlreadyExistsException(pe);
            } else if (pe.getMessage().contains("account_email_key")) {
                throw ApplicationExceptionFactory.createEmailAlreadyExistsException(pe);
            } else {
                throw pe;
            }
        }
    }
}
