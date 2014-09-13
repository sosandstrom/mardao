package net.sf.mardao.dao;

import java.io.IOException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Stores entities in Google App Engine's Datastore.
 *
 * @author osandstrom Date: 2014-09-13 Time: 17:43
 */
public class DatastoreSupplier implements Supplier<Key, Entity, Entity> {

  private DatastoreService syncService;

  private DatastoreService getSyncService() {
    if (null == syncService) {
      syncService = DatastoreServiceFactory.getDatastoreService();
    }
    return syncService;
  }

  @Override
  public Entity readValue(Key key) throws IOException {
    try {
      return getSyncService().get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  @Override
  public Key writeValue(Key key, Entity value) throws IOException {
    return getSyncService().put(value);
  }

  @Override
  public Key toKey(String kind, Long lId) {
    return KeyFactory.createKey(null, kind, lId);
  }

  @Override
  public Key toKey(String kind, String sId) {
    return KeyFactory.createKey(null, kind, sId);
  }

  @Override
  public Long toLongKey(Object key) {
    return null != key ? ((Key) key).getId() : null;
  }

  @Override
  public String toStringKey(Object key) {
    return null != key ? ((Key) key).getName() : null;
  }

  @Override
  public String getString(Entity value, String column) {
    return (String) value.getProperty(column);
  }

  @Override
  public Long getLong(Entity value, String column) {
    return (Long) value.getProperty(column);
  }

  @Override
  public void setLong(Object value, String column, Long l) {
    ((Entity) value).setProperty(column, l);
  }

  @Override
  public void setString(Object value, String column, String s) {
    ((Entity) value).setProperty(column, s);
  }

  @Override
  public Entity createWriteValue(Key key) {
    return new Entity(key);
  }
}
