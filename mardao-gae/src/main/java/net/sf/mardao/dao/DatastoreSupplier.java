package net.sf.mardao.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;

/**
 * Stores entities in Google App Engine's Datastore.
 *
 * @author osandstrom Date: 2014-09-13 Time: 17:43
 */
public class DatastoreSupplier implements Supplier<Key, Entity, Entity, Transaction> {

  static final Logger LOGGER = LoggerFactory.getLogger(DatastoreSupplier.class);

  private DatastoreService syncService;

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
  public int count(Transaction tx, String kind, Key ancestorKey, Key simpleKey, Filter... filters) {
    final PreparedQuery pq = prepare(kind, true, ancestorKey, simpleKey, null, false, null, false, null, filters);
    return pq.countEntities(FetchOptions.Builder.withDefaults());
  }

  @Override
  public void deleteValue(Transaction tx, Key key) throws IOException {
    getSyncService().delete(key);
  }

  @Override
  public void deleteValues(Transaction tx, Collection<Key> keys) throws IOException {
    getSyncService().delete(keys);
  }

  @Override
  public Iterable<Entity> queryIterable(Transaction tx, String kind, boolean keysOnly, int offset, int limit,
                                        Key ancestorKey, Key simpleKey,
                                        String primaryOrderBy, boolean primaryIsAscending,
                                        String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
    final PreparedQuery pq = prepare(kind, keysOnly, ancestorKey, simpleKey,
      primaryOrderBy, primaryIsAscending,
      secondaryOrderBy, secondaryIsAscending, null, filters);

    final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, offset, limit);
    return _iterable;
  }

  @Override
  public CursorPage<Entity> queryPage(Transaction tx, String kind, boolean keysOnly,
                                         int requestedPageSize, Key ancestorKey,
                                    String primaryOrderBy, boolean primaryIsAscending,
                                    String secondaryOrderBy, boolean secondaryIsAscending,
                                    Collection<String> projections,
                                    String cursorString,
                                    Filter... filters) {

    final PreparedQuery pq = prepare(kind, keysOnly, ancestorKey, null,
      primaryOrderBy, primaryIsAscending,
      secondaryOrderBy, secondaryIsAscending,
      projections, filters);

    final QueryResultList<Entity> iterable = asQueryResultList(pq, requestedPageSize, cursorString);

    final CursorPage<Entity> cursorPage = new CursorPage<Entity>();

    // if first page and populate totalSize, fetch this with async query:
    if (null == cursorString) {
      int count = count(tx, kind, ancestorKey, null, filters);
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
  public Entity queryUnique(Transaction tx, Key ancestorKey, String kind, Filter... filters) {
    final PreparedQuery pq = prepare(kind, false, ancestorKey, null,
      null, false, null, false,
      null, filters);
    final Entity entity = pq.asSingleEntity();
    return entity;
  }

  @Override
  public Entity readValue(Transaction tx, Key key) throws IOException {
    try {
      final Entity value = getSyncService().get(tx, key);
      return value;
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  @Override
  public Key writeValue(Transaction tx, Key key, Entity value) throws IOException {
    return getSyncService().put(tx, value);
  }

  @Override
  public Key toKey(Key parentKey, String kind, Long lId) {
    return null != lId ? KeyFactory.createKey(parentKey, kind, lId) : null;
  }

  @Override
  public Key toKey(Key parentKey, String kind, String sId) {
    return null != sId ? KeyFactory.createKey(parentKey, kind, sId) : null;
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
  public Collection getCollection(Entity value, String column) {
    return (Collection) value.getProperty(column);
  }

  @Override
  public Date getDate(Entity value, String column) {
    return (Date) value.getProperty(column);
  }

  @Override
  public Long getLong(Entity value, String column) {
    return (Long) value.getProperty(column);
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
  public String getString(Entity value, String column) {
    return (String) value.getProperty(column);
  }

  @Override
  public Integer getInteger(Entity value, String column) {
    Long longValue = (Long)value.getProperty(column);
    return null != longValue ? longValue.intValue() : null;
  }

  @Override
  public Boolean getBoolean(Entity value, String column) {
    return (Boolean) value.getProperty(column);
  }

  @Override
  public Float getFloat(Entity value, String column) {
    return (Float) value.getProperty(column);
  }

  @Override
  public void setCollection(Entity value, String column, Collection c) {
    value.setProperty(column, c);
  }

  @Override
  public void setDate(Entity value, String column, Date d) {
    value.setProperty(column, d);
  }

  @Override
  public void setLong(Entity value, String column, Long l) {
    value.setProperty(column, l);
  }

  @Override
  public void setString(Entity value, String column, String s) {
    value.setProperty(column, s);
  }

  @Override
  public void setInteger(Entity value, String column, Integer i) {
    value.setProperty(column, i);
  }

  @Override
  public void setBoolean(Entity value, String column, Boolean b) {
    value.setProperty(column, b);
  }

  @Override
  public void setFloat(Entity value, String column, Float f) {
    value.setProperty(column, f);
  }

  @Override
  public Entity createWriteValue(Key parentKey, String kind, Long id) {
    return null != id ? new Entity(kind, id, parentKey) : new Entity(kind, parentKey);
  }

  @Override
  public Entity createWriteValue(Key parentKey, String kind, String id) {
    return null != id ? new Entity(kind, id, parentKey) : new Entity(kind, parentKey);
  }

  private DatastoreService getSyncService() {
    if (null == syncService) {
      syncService = DatastoreServiceFactory.getDatastoreService();
    }
    return syncService;
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
      case LESS_THAN:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.GREATER_THAN, mardaoFilter.getOperand());
      default:
        throw new UnsupportedOperationException("No such Filter Operator " + mardaoFilter.getOperator());
    }
  }
}
