package net.sf.mardao.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.persist.PersistService;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom
 * Date: 1/19/14 Time: 8:12 PM
 */
public class MardaoGuicePersistService implements PersistService {
  static final Logger LOGGER = LoggerFactory.getLogger(MardaoGuicePersistService.class);
  private DatastoreService datastore;

  public MardaoGuicePersistService() {
    LOGGER.debug("<init>");
  }

  public DatastoreService getDatastore() {
    return datastore;
  }

  @Override
  public void start() {
    LOGGER.debug("start");
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void stop() {
    LOGGER.debug("stop");
  }
}
