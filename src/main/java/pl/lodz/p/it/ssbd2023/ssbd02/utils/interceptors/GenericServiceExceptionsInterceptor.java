package pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors;

import jakarta.ejb.AccessLocalException;
import jakarta.ejb.EJBAccessException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;

public class GenericServiceExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext ictx) {
    try {
      return ictx.proceed();
    } catch (OptimisticLockException ole) {
      throw ApplicationExceptionFactory.createApplicationOptimisticLockException();
    } catch (BaseWebApplicationException be) {
      throw be;
    } catch (EJBAccessException | AccessLocalException ae) {
      throw ApplicationExceptionFactory.createAccessDeniedException(ae);
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }
}
