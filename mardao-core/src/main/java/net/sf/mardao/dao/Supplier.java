package net.sf.mardao.dao;

import java.io.IOException;
import java.util.Date;

import net.sf.mardao.core.filter.Filter;

/**
 * Reads and Writes to physical store.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:50
 */
public interface Supplier<K, RV, WV> {
  int count(String kind, Object ancestorKey, Object simpleKey, Filter... filters);
  void deleteValue(K key) throws IOException;
  RV readValue(K key) throws IOException;
  K writeValue(K key, WV value) throws IOException;

  K toKey(String kind, Long lId);
  K toKey(String kind, String sId);

  Long toLongKey(Object key);
  String toStringKey(Object key);

  Date getDate(RV value, String column);
  Long getLong(RV value, String column);
  String getString(RV value, String column);

  void setDate(Object value, String column, Date d);
  void setLong(Object value, String column, Long l);
  void setString(Object value, String column, String s);

  WV createWriteValue(K key);

  // --- query methods ---

  Iterable<RV> queryIterable(String kind, boolean keysOnly,
                              int offset, int limit,
                              Object ancestorKey, Object simpleKey,
                              String primaryOrderBy, boolean primaryIsAscending,
                              String secondaryOrderBy, boolean secondaryIsAscending,
                              Filter... filters);

  RV queryUnique(String kind, Filter... filters);
}
