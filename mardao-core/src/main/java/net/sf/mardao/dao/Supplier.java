package net.sf.mardao.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;

/**
 * Reads and Writes to physical store.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:50
 */
public interface Supplier<K, RV, WV, T> {
  int count(T tx, String kind, K ancestorKey, K simpleKey, Filter... filters);
  void deleteValue(T tx, K key) throws IOException;
    void deleteValues(T tx, Collection<K> keys) throws IOException;
  RV readValue(T tx, K key) throws IOException;
  K writeValue(T tx, K key, WV value) throws IOException;

  K toKey(K parentKey, String kind, Long lId);
  K toKey(K parentKey, String kind, String sId);

  Long toLongKey(K key);
  String toStringKey(K key);
  K toParentKey(K key);

  Collection getCollection(RV value, String column);
  Date getDate(RV value, String column);
  Long getLong(RV value, String column);
  K getKey(RV value, String column);
  K getParentKey(RV value, String column);
  String getString(RV value, String column);
  Integer getInteger(RV value, String column);
  Boolean getBoolean(RV value, String column);
  Float getFloat(RV value, String column);

  void setCollection(WV value, String column, Collection c);
  void setDate(WV value, String column, Date d);
  void setLong(WV value, String column, Long l);
  void setString(WV value, String column, String s);
  void setInteger(WV value, String column, Integer i);
  void setBoolean(WV value, String column, Boolean b);
  void setFloat(WV value, String column, Float f);

  WV createWriteValue(K parentKey, String kind, Long id);
  WV createWriteValue(K parentKey, String kind, String id);

  // --- transaction methods ---

  T beginTransaction();
  void commitTransaction(T transaction);
  void rollbackActiveTransaction(T transaction);

  // --- query methods ---

  Iterable<RV> queryIterable(T tx, String kind, boolean keysOnly,
                              int offset, int limit,
                              K ancestorKey, K simpleKey,
                              String primaryOrderBy, boolean primaryIsAscending,
                              String secondaryOrderBy, boolean secondaryIsAscending,
                              Filter... filters);

  RV queryUnique(T tx, K parentKey, String kind, Filter... filters);

  CursorPage<RV> queryPage(T tx, String kind, boolean keysOnly,
                           int requestedPageSize, K ancestorKey,
                          String primaryOrderBy, boolean primaryIsAscending,
                          String secondaryOrderBy, boolean secondaryIsAscending,
                          Collection<String> projections,
                          String cursorString,
                          Filter... filters);

}
