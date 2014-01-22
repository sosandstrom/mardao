package net.sf.mardao.guice;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Transaction;
import com.google.inject.persist.Transactional;

import net.sf.mardao.core.dao.TypeDaoImpl;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 1/21/14 Time: 8:59 PM
 */
public class MardaoTransactionManager implements MethodInterceptor {
  static final Logger LOGGER = LoggerFactory.getLogger(MardaoTransactionManager.class);

  /**
   * Implement this method to perform extra treatments before and after the invocation. Polite implementations would certainly like
   * to invoke {@link org.aopalliance.intercept.Joinpoint#proceed()}.
   *
   * @param invocation the method invocation joinpoint
   * @return the result of the call to {@link org.aopalliance.intercept.Joinpoint#proceed()}, might be intercepted by the
   *         interceptor.
   * @throws Throwable if the interceptors or the target-object throws an exception.
   */
  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    final Method method = invocation.getMethod();
    final Transactional annotation = method.getAnnotation(Transactional.class);

    Object result;
    if (null == annotation) {
      result = invocation.proceed();
    }
    else {
      LOGGER.info("--- beginTransaction for {}()", method.getName());
      final Transaction tx = TypeDaoImpl.beginTransactionImpl();
      try {
        result = invocation.proceed();
        TypeDaoImpl.commitTransactionImpl(tx);
        LOGGER.info("=== committedTransaction for {}()", method.getName());
      }
      finally {
        TypeDaoImpl.rollbackActiveTransactionImpl(tx);
      }
    }

    return result;
  }
}
