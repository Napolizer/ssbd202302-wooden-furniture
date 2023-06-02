package pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;

public class ProductGroupFacadeExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext ictx) throws Exception {
    try {
      return ictx.proceed();
    } catch (OptimisticLockException ole) {
      throw ole;
    } catch (PersistenceException pe) {
      if (pe.getMessage().contains("product_group_name_key")) {
        throw ApplicationExceptionFactory.createProductGroupAlreadyExistsException();
      }  else {
        throw pe;
      }
    }
  }
}
