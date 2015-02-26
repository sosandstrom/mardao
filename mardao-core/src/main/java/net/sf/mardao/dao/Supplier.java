package net.sf.mardao.dao;

/*
 * #%L
 * mardao-core
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Future;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;

/**
 * Reads and Writes to physical store.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:50
 */
public interface Supplier<K, RV, WV, T> {
  int count(T tx, Mapper mapper, K ancestorKey, K simpleKey, Filter... filters);
  void deleteValue(T tx, K key) throws IOException;
  void deleteValues(T tx, Collection<K> keys) throws IOException;
  RV readValue(T tx, Mapper mapper, K key) throws IOException;
  K writeValue(T tx, K key, WV value) throws IOException;
  K insertValue(T tx, K key, WV value) throws IOException;

  Future<RV> readFuture(T tx, Mapper mapper, K key) throws IOException;
  Future<K> writeFuture(T tx, K key, WV value) throws IOException;

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
  ByteBuffer getByteBuffer(RV value, String column);

  Collection getWriteCollection(WV value, String column);
  Date getWriteDate(WV value, String column);
  Long getWriteLong(WV value, String column);
  K getWriteKey(WV value, String column);
  K getWriteParentKey(WV value, String column);
  String getWriteString(WV value, String column);
  Integer getWriteInteger(WV value, String column);
  Boolean getWriteBoolean(WV value, String column);
  Float getWriteFloat(WV value, String column);
  ByteBuffer getWriteByteBuffer(WV value, String column);

  void setCollection(WV value, String column, Collection c);
  void setDate(WV value, String column, Date d);
  void setLong(WV value, String column, Long l);
  void setString(WV value, String column, String s);
  void setInteger(WV value, String column, Integer i);
  void setBoolean(WV value, String column, Boolean b);
  void setFloat(WV value, String column, Float f);
  void setByteBuffer(WV value, String column, ByteBuffer b);

  WV createWriteValue(Mapper mapper, K parentKey, Long id, Object entity);
  WV createWriteValue(Mapper mapper, K parentKey, String id, Object entity);
  void setPrimaryKey(WV value, Mapper mapper, String column, K primaryKey, Object Entity);
  void setParentKey(WV value, Mapper mapper, String column, K parentKey, Object Entity);

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

  CursorPage<RV> queryPage(T tx, Mapper mapper, boolean keysOnly,
                           int requestedPageSize, K ancestorKey,
                          String primaryOrderBy, boolean primaryIsAscending,
                          String secondaryOrderBy, boolean secondaryIsAscending,
                          Collection<String> projections,
                          String cursorString,
                          Filter... filters);

}
