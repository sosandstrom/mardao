package net.sf.mardao.guice;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

/**
 * Binds {@link UnitOfWork}, {@link PersistService} and {@link MardaoTransactionManager}.
 *
 * @author osandstrom 
 * Date: 1/19/14 Time: 8:59 PM
 */
public class MardaoGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
          bind(UnitOfWork.class).to(MardaoGuiceUnitOfWork.class);
          bind(PersistService.class).to(MardaoGuicePersistService.class);

          MardaoTransactionManager transactionManager = new MardaoTransactionManager();
          requestInjection(transactionManager);
          bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionManager);
    }
}
