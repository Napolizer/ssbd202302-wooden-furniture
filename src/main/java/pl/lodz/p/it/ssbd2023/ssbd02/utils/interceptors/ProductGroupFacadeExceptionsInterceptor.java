package pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;

public class ProductGroupFacadeExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext ictx) throws Exception {
    try {
      return ictx.proceed();
    } catch (OptimisticLockException ole) {
      throw ole;
    } catch (PersistenceException pe) {
      String msg = pe.getMessage();
      if (msg.contains("product_group_name_key") || msg.contains("product_group.NAME")) {
        throw ApplicationExceptionFactory.createProductGroupAlreadyExistsException();
      } else {
        if (pe.getCause() instanceof BaseWebApplicationException exception) {
          throw exception;
        }
        throw pe;
      }
    }
  }
}
