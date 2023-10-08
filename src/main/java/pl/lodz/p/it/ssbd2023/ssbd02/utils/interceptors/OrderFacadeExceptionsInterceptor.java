package pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;

public class OrderFacadeExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext ictx) throws Exception {
    try {
      return ictx.proceed();
    } catch (OptimisticLockException ole) {
      throw ole;
    } catch (PersistenceException pe) {
      String msg = pe.getMessage();
      if (msg.contains("order_name_key") || msg.contains("order.NAME")) {
        throw ApplicationExceptionFactory.createOrderAlreadyExistsException();
      } else {
        throw pe;
      }
    }
  }
}
