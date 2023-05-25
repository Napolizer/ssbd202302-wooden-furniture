package pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerInterceptor {
  private static final Logger logger = Logger.getLogger(LoggerInterceptor.class.getName());
  @Resource
  private SessionContext sctx;

  @AroundInvoke
  public Object intercept(InvocationContext context) throws Exception {
    StringBuilder message = new StringBuilder("Method interception:\n");
    Object result;
    try {
      try {
        message.append("Method: ").append(context.getMethod().toString()).append("\n");
        message.append("User: ").append(sctx.getCallerPrincipal().getName()).append("\n");
        message.append("Parameters: [ ");
        Object[] methodParameters = context.getParameters();
        if (methodParameters != null) {
          for (Object param : methodParameters) {
            if (param instanceof String) {
              message.append(maskString((String) param)).append(" ");
            } else {
              message.append(param).append(" ");
            }
          }
        }
        message.append(" ]\n");
      } catch (Exception e) {
        message.append(" ]\n");
        logger.log(Level.SEVERE, "Exception in the LoggerInterceptor: ", e);
        throw e;
      }
      result = context.proceed();
    } catch (Exception e) {
      message.append("Terminated with an exception ").append(e);
      logger.log(Level.SEVERE, message.toString(), e);
      throw e;
    }
    if (result instanceof String) {
      message.append(maskString((String) result)).append(" ");
    } else {
      message.append("Return value: ").append(result).append(" ");
    }

    logger.info(message.toString());
    return result;
  }

  private String maskString(String password) {
    // Replace each character of the password with a question mark
    return password.replaceAll(".", "?");
  }
}
