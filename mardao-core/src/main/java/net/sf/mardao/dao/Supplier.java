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
  int count(TransactionHolder tx, String kind, Object ancestorKey, Object simpleKey, Filter... filters);
  void deleteValue(TransactionHolder tx, K key) throws IOException;
  RV readValue(TransactionHolder tx, K key) throws IOException;
  K writeValue(TransactionHolder tx, K key, WV value) throws IOException;

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

  // --- transaction methods ---

  Object beginTransaction();
  void commitTransaction(TransactionHolder holder);
  void rollbackActiveTransaction(TransactionHolder transaction);

  // --- query methods ---

  Iterable<RV> queryIterable(TransactionHolder tx, String kind, boolean keysOnly,
                              int offset, int limit,
                              Object ancestorKey, Object simpleKey,
                              String primaryOrderBy, boolean primaryIsAscending,
                              String secondaryOrderBy, boolean secondaryIsAscending,
                              Filter... filters);

  RV queryUnique(TransactionHolder tx, String kind, Filter... filters);
}
