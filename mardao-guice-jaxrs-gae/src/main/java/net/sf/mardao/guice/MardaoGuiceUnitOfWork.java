package net.sf.mardao.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Transaction;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.persist.UnitOfWork;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 1/19/14 Time: 8:16 PM
 */
public class MardaoGuiceUnitOfWork implements UnitOfWork {
  static final Logger LOGGER = LoggerFactory.getLogger(MardaoGuiceUnitOfWork.class);

  public MardaoGuiceUnitOfWork() {
    LOGGER.debug("<init>");
  }

  @Override
  public void begin() {
    LOGGER.debug("begin()");
  }

  @Override
  public void end() {
    LOGGER.debug("end()");
  }

}
