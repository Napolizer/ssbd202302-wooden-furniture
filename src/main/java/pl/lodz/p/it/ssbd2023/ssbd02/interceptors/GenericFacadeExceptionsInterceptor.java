package pl.lodz.p.it.ssbd2023.ssbd02.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;

import java.sql.SQLException;

public class GenericFacadeExceptionsInterceptor {
    @AroundInvoke
    public Object intercept(InvocationContext ictx) {
        try {
            return ictx.proceed();
        } catch (OptimisticLockException ole) {
            throw ApplicationExceptionFactory.createApplicationOptimisticLockException();
        } catch (PersistenceException | SQLException pe) {
            throw ApplicationExceptionFactory.createGeneralPersistenceException(pe);
        } catch (BaseWebApplicationException be) {
            throw be;
        } catch (Exception e) {
            throw ApplicationExceptionFactory.createUnknownErrorException(e);
        }
    }
}
