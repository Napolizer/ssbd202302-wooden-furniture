package pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors;

import jakarta.ejb.AccessLocalException;
import jakarta.ejb.EJBAccessException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;

public class GenericEndpointExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext ictx) throws Exception {
    try {
      return ictx.proceed();
    } catch (AuthenticationException | BaseWebApplicationException be) {
      throw be;
    } catch (EJBAccessException | AccessLocalException ae) {
      throw ApplicationExceptionFactory.createAccessDeniedException(ae);
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }
}
