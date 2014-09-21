package net.sf.mardao.dao;

import java.io.IOException;
import java.util.Collection;

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

import net.sf.mardao.core.filter.Filter;

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
  public Iterable<Entity> queryIterable(String kind, boolean keysOnly, int offset, int limit,
                                        Object ancestorKey, Object simpleKey,
                                        String primaryOrderBy, boolean primaryIsAscending,
                                        String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
    final PreparedQuery pq = prepare(kind, keysOnly, (Key)ancestorKey, (Key)simpleKey,
      primaryOrderBy, primaryIsAscending,
      secondaryOrderBy, secondaryIsAscending, null, filters);

    final QueryResultIterable<Entity> _iterable = asQueryResultIterable(pq, offset, limit);
    return _iterable;
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

    // filter on keyName:
    if (null != simpleKey) {
      q.addFilter(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, simpleKey);
    }

    // filter query:
    if (null != filters) {
      for(Filter f : filters) {
        q.setFilter(createFilter(f));
      }
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

  protected static com.google.appengine.api.datastore.Query.Filter createFilter(Filter mardaoFilter) {
    switch (mardaoFilter.getOperator()) {
      case EQUALS:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.EQUAL, mardaoFilter.getOperand());
      case IN:
        return new Query.FilterPredicate(mardaoFilter.getColumn(), Query.FilterOperator.IN, mardaoFilter.getOperand());
      default:
        throw new UnsupportedOperationException("No such Filter Operator " + mardaoFilter.getOperator());
    }
  }
}
