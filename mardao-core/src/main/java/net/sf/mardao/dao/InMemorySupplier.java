package net.sf.mardao.dao;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stores entities in-memory using a new TreeMap&lt;String, Map&lt;String, Object&gt;&gt;.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:48
 */
public class InMemorySupplier implements Supplier<String, Map<String, Object>, Map<String, Object>> {

  private final Map<String, Map<String, Object>> store = new TreeMap<String, Map<String, Object>>();

  @Override
  public Map<String, Object> readValue(String key) throws IOException {
    return store.get(key);
  }

  @Override
  public String getString(Map<String, Object> core, String column) {
    return (String) core.get(column);
  }

  @Override
  public Long getLong(Map<String, Object> core, String column) {
    return (Long) core.get(column);
  }

  @Override
  public void setLong(Object value, String column, Long l) {
    ((Map<String, Object>) value).put(column, l);
  }

  @Override
  public void setString(Object value, String column, String s) {
    ((Map<String, Object>) value).put(column, s);
  }

  @Override
  public Map<String, Object> createWriteValue(String key) {
    return new TreeMap<String, Object>();
  }

  @Override
  public String writeValue(String key, Map<String, Object> core) throws IOException {
    store.put(key, core);
    return key;
  }

  @Override
  public String toKey(String kind, Long lId) {
    return null != lId ? lId.toString() : null;
  }

  @Override
  public String toKey(String kind, String sId) {
    return sId;
  }

  @Override
  public Long toLongKey(Object key) {
    return null != key ? Long.parseLong(key.toString()) : null;
  }

  @Override
  public String toStringKey(Object key) {
    return (String) key;
  }
}
