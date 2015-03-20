package net.sf.mardao.dao;

/*
 * #%L
 * mardao-gae
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

import com.google.appengine.api.datastore.*;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Stores entities in Google App Engine's Datastore.
 *
 * @author osandstrom Date: 2014-09-13 Time: 17:43
 */
public class DatastoreSupplier extends AbstractSupplier<Key, Entity, Entity, Transaction> {

  private DatastoreService syncService;
  private AsyncDatastoreService asyncService;

  @Override
  public void rollbackActiveTransaction(Transaction tx) {
    if (tx.isActive()) {
      tx.rollback();
    }
  }

  @Override
  public void commitTransaction(Transaction tx) {
    tx.commit();
  }

  @Override
  public Transaction beginTransaction() {
    TransactionOptions options = TransactionOptions.Builder.withXG(true);
    return getSyncService().beginTransaction(options);
  }

  @Override
  public int count(Transaction tx, Mapper mapper, Key ancestorKey, Key simpleKey, Filter... filters) {
    final PreparedQuery pq = prepare(mapper.getKind(), true, ancestorKey, simpleKey, null, false, null, false, null, filters);
    return pq.countEntities(FetchOptions.Builder.withDefaults());
  }

  @Override
  public void deleteValue(Transaction tx, Mapper mapper, Key key) throws IOException {
    getSyncService().delete(key);
  }

  @Override
  public void deleteValues(Transaction tx, Mapper mapper, Collection<Key> keys) throws IOException {
    getSyncService().delete(keys);
  }

  @Override
  public Iterable<Entity> queryIterable(Transaction tx, Mapper mapper, boolean keysOnly, int offset, int limit,
                                        Key ancestorKey, Key simpleKey,
                                        String primaryOrderBy, boolean primaryIsAscending,
                                        String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
    final PreparedQuery pq = prepare(mapper.getKind(), keysOnly, ancestorKey, simpleKey,
      primaryOrderBy, primaryIsAscending,
      secondaryOrderBy, secondaryIsAscending, null, filters);

    final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, offset, limit);
    return _iterable;
  }

  @Override
  public CursorPage<Entity> queryPage(Transaction tx, Mapper mapper, boolean keysOnly,
                                         int requestedPageSize, Key ancestorKey,
                                    String primaryOrderBy, boolean primaryIsAscending,
                                    String secondaryOrderBy, boolean secondaryIsAscending,
                                    Collection<String> projections,
                                    String cursorString,
                                    Filter... filters) {

    final PreparedQuery pq = prepare(mapper.getKind(), keysOnly, ancestorKey, null,
      primaryOrderBy, primaryIsAscending,
      secondaryOrderBy, secondaryIsAscending,
      projections, filters);

    final QueryResultList<Entity> iterable = asQueryResultList(pq, requestedPageSize, cursorString);

    final CursorPage<Entity> cursorPage = new CursorPage<Entity>();

    // if first page and populate totalSize, fetch this with async query:
    if (null == cursorString) {
      int count = count(tx, mapper, ancestorKey, null, filters);
      cursorPage.setTotalSize(count);
    }

    cursorPage.setItems(iterable);

    // only if next is available
    if (iterable.size() == requestedPageSize) {

      // only if page size != total size
      if (null == cursorPage.getTotalSize() || iterable.size() < cursorPage.getTotalSize()) {
        cursorPage.setCursorKey(iterable.getCursor().toWebSafeString());
      }
    }

    return cursorPage;
  }
  @Override
  public Entity queryUnique(Transaction tx, Mapper mapper, Key ancestorKey, Filter... filters) {
    final PreparedQuery pq = prepare(mapper.getKind(), false, ancestorKey, null,
      null, false, null, false,
      null, filters);
    final Entity entity = pq.asSingleEntity();
    return entity;
  }

  @Override
  public Future<Entity> readFuture(Transaction tx, Mapper mapper, Key key) throws IOException {
    return getAsyncService().get(tx, key);
  }

  @Override
  public Entity readValue(Transaction tx, Mapper mapper, Key key) throws IOException {
    try {
      final Entity value = getSyncService().get(tx, key);
      return value;
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  @Override
  public Future<Key> writeFuture(Transaction transaction, Mapper mapper, Key key, Entity entity) throws IOException {
    return getAsyncService().put(transaction, entity);
  }

  @Override
  public Key writeValue(Transaction tx, Mapper mapper, Key key, Entity value) throws IOException {
    return getSyncService().put(tx, value);
  }

  @Override
  public Key toKey(Key parentKey, String kind, Serializable id) {
    if (null == id) {
      return null;
    }
    return id instanceof String ? KeyFactory.createKey(parentKey, kind, (String) id) : KeyFactory.createKey(parentKey, kind, (Long) id);
  }

  @Override
  public Long toLongKey(Key key) {
    return null != key ? key.getId() : null;
  }

  @Override
  public String toStringKey(Key key) {
    return null != key ? key.getName() : null;
  }

  @Override
  public Key toParentKey(Key key) {
    return null != key ? key.getParent() : null;
  }

  @Override
  protected Object getReadObject(Entity value, String column) {
    return value.getProperty(column);
  }

  @Override
  public Key getKey(Entity value, String column) {
    return value.getKey();
  }

  @Override
  public Key getParentKey(Entity value, String column) {
    return value.getParent();
  }

  @Override
  public Integer getInteger(Entity value, String column) {
    Long longValue = (Long)value.getProperty(column);
    return null != longValue ? longValue.intValue() : null;
  }

  @Override
  public Float getFloat(Entity value, String column) {
    Double doubleValue = (Double) value.getProperty(column);
    return null != doubleValue ? doubleValue.floatValue() : null;
  }

  @Override
  public ByteBuffer getByteBuffer(Entity value, String column) {
    Blob blob = (Blob) value.getProperty(column);
    return null != blob ? ByteBuffer.wrap(blob.getBytes()) : null;
  }

  @Override
  protected void setObject(Entity value, String column, Object o) {
    value.setProperty(column, o);
  }

  @Override
  public void setString(Entity value, String column, String s) {
    value.setProperty(column, null == s || s.length() <= 500 ? s : new Text(s));
  }

  @Override
  public void setByteBuffer(Entity value, String column, ByteBuffer b) {
    value.setProperty(column, null != b ? new Blob(b.array()) : null);
  }

  @Override
  public Entity createWriteValue(Mapper mapper, Key parentKey, Long id, Object entity) {
    return null != id ? new Entity(mapper.getKind(), id, parentKey) : new Entity(mapper.getKind(), parentKey);
  }

  @Override
  public Entity createWriteValue(Mapper mapper, Key parentKey, String id, Object entity) {
    return null != id ? new Entity(mapper.getKind(), id, parentKey) : new Entity(mapper.getKind(), parentKey);
  }

  private DatastoreService getSyncService() {
    if (null == syncService) {
      syncService = DatastoreServiceFactory.getDatastoreService();
    }
    return syncService;
  }

  private AsyncDatastoreService getAsyncService() {
    if (null == asyncService) {
      asyncService = DatastoreServiceFactory.getAsyncDatastoreService();
    }
    return asyncService;
  }

  /**
   *
   * @param keysOnly
   * @param ancestorKey
   * @param orderBy
   * @param ascending
   * @param filters
   * @return
   */
  protected PreparedQuery prepare(String kind, boolean keysOnly, Key ancestorKey, Key simpleKey,
                                  String orderBy, boolean ascending,
                                  String secondaryOrderBy, boolean secondaryAscending, Collection<String> projections, Filter... filters) {
//    LOG.debug("prepare {} with filters {}", getTableName(), filters);

    Query q = new Query(kind, ancestorKey);

    // keys only?
    if (keysOnly) {
      q.setKeysOnly();
    }

    // filter query:
    final List<Query.Filter> queryFilters = new ArrayList<Query.Filter>(filters.length);

    // filter on keyName:
    if (null != simpleKey) {
      queryFilters.add(new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, simpleKey));
    }

    // additional filters
    if (filters.length > 0) {
      for (Filter f: filters) {
        queryFilters.add(createFilter(f));
      }
    }

    if (queryFilters.size() == 1) {
      q.setFilter(queryFilters.get(0));
    } else if (queryFilters.size() > 1) {
      q.setFilter(Query.CompositeFilterOperator.and(queryFilters));
    }

    // sort query?
    if (null != orderBy) {
      q.addSort(orderBy, ascending ? Query.SortDirection.ASCENDING : Query.SortDirection.DESCENDING);

      // secondary sort order?
      if (null != secondaryOrderBy) {
        q.addSort(secondaryOrderBy, secondaryAscending ? Query.SortDirection.ASCENDING : Query.SortDirection.DESCENDING);
      }
    }

    // Add projections
//    if (null != projections) {
//      for (String projection : projections) {
//        q.addProjection(new PropertyProjection(projection, getColumnClass(projection)));
//      }
//    }

    return getSyncService().prepare(/* TRANSACTION.get(),*/ q);
  }

  protected static QueryResultIterable<Entity> asQueryResultIterable(PreparedQuery pq, int offset, int limit) {
    FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

    if (0 < limit) {
      fetchOptions.limit(limit);
    }

    if (0 < offset) {
      fetchOptions.offset(offset);
    }

    return pq.asQueryResultIterable(fetchOptions);
  }

  protected QueryResultList<Entity> asQueryResultList(PreparedQuery pq, int pageSize, String cursorString) {
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);

    if (null != cursorString) {
      fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
    }

    return pq.asQueryResultList(fetchOptions);
  }

  protected static com.google.appengine.api.datastore.Query.Filter createFilter(Filter mardaoFilter) {
    switch (mardaoFilter.getOperator()) {
      case EQUALS:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.EQUAL, mardaoFilter.getOperand());
      case IN:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.IN, mardaoFilter.getOperand());
      case GREATER_THAN:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.GREATER_THAN, mardaoFilter.getOperand());
      case GREATER_THAN_OR_EQUALS:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.GREATER_THAN_OR_EQUAL, mardaoFilter.getOperand());
      case LESS_THAN:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.LESS_THAN, mardaoFilter.getOperand());
      case NOT_EQUALS:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.NOT_EQUAL, mardaoFilter.getOperand());
      default:
        throw new UnsupportedOperationException("No such Filter Operator " + mardaoFilter.getOperator());
    }
  }
}
