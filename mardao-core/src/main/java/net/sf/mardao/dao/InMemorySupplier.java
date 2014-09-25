package net.sf.mardao.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.mardao.core.filter.Filter;

/**
 * Stores entities in-memory using a new TreeMap&lt;InMemoryKey, Map&lt;String, Object&gt;&gt;.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:48
 */
public class InMemorySupplier implements Supplier<InMemoryKey, Map<String, Object>, Map<String, Object>> {

  private final Map<String, Map<String, Map<String, Object>>> store = new TreeMap<String, Map<String, Map<String, Object>>>();

  @Override
  public int count(String kind, Object ancestorKey, Object simpleKey, Filter... filters) {
    final Collection<Map<String, Object>> filtered = filterValues(kindStore(kind).values());
    return filtered.size();
  }

  @Override
  public void deleteValue(InMemoryKey key) throws IOException {
    kindStore(key).remove(key.getName());
  }

  @Override
  public Map<String, Object> readValue(InMemoryKey key) throws IOException {
    return kindStore(key).get(key.getName());
  }

  @Override
  public Date getDate(Map<String, Object> value, String column) {
    return (Date) value.get(column);
  }

  @Override
  public Long getLong(Map<String, Object> core, String column) {
    return (Long) core.get(column);
  }

  @Override
  public String getString(Map<String, Object> core, String column) {
    return (String) core.get(column);
  }

  @Override
  public void setDate(Object value, String column, Date d) {
    ((Map<String, Object>) value).put(column, d);
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
  public Map<String, Object> createWriteValue(InMemoryKey key) {
    return new TreeMap<String, Object>();
  }

  @Override
  public Iterable<Map<String, Object>> queryIterable(String kind, boolean keysOnly, int offset, int limit,
                                                     Object ancestorKey, Object simpleKey,
                                                     String primaryOrderBy, boolean primaryIsAscending,
                                                     String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
    // this will do for now
    Collection<Map<String, Object>> remaining = kindStore(kind).values();

    return filterValues(remaining, filters);
  }

  private Collection<Map<String, Object>> filterValues(Collection<Map<String, Object>> values, Filter... filters) {
    Collection<Map<String, Object>> after = values;
    if (null != filters) {
      for (Filter f : filters) {
        after = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> v : values) {
          if (match(v, f)) {
            after.add(v);
          }
        }
        values = after;
        if (after.isEmpty()) {
          break;
        }
      }
    }
    return after;
  }

  @Override
  public Map<String, Object> queryUnique(String kind, Filter... filters) {
    final Iterable<Map<String, Object>> iterable = queryIterable(kind, false, 0, 1,
      null, null, null, false, null, false, filters);
    final Iterator<Map<String, Object>> iterator = iterable.iterator();
    return iterator.hasNext() ? iterator.next() : null;
  }

  @Override
  public InMemoryKey writeValue(InMemoryKey key, Map<String, Object> core) throws IOException {
    kindStore(key).put(key.getName(), core);
    return key;
  }

  @Override
  public InMemoryKey toKey(String kind, Long lId) {
    return InMemoryKey.of(kind, null != lId ? lId.toString() : null);
  }

  @Override
  public InMemoryKey toKey(String kind, String sId) {
    return InMemoryKey.of(kind, sId);
  }

  @Override
  public Long toLongKey(Object key) {
    return null != key ? Long.parseLong(((InMemoryKey) key).getName()) : null;
  }

  @Override
  public String toStringKey(Object key) {
    return null != key ? ((InMemoryKey) key).getName() : null;
  }

  protected Map<String, Map<String, Object>> kindStore(InMemoryKey key) {
    return kindStore(key.getKind());
  }

  protected Map<String, Map<String, Object>> kindStore(String kind) {
    Map<String, Map<String, Object>> ks = store.get(kind);
    if (null == ks && null != kind) {
      ks = new TreeMap<String, Map<String, Object>>();
      store.put(kind, ks);
    }
    return ks;
  }

  protected static boolean match(Map<String, Object> v, Filter f) {
    final Object value = v.get(f.getColumn());
    if (null == f.getOperand()) {
      return null == value;
    }
    switch (f.getOperator()) {
      case EQUALS:
        return f.getOperand().equals(value);
      default:
        throw new UnsupportedOperationException("match " + f.getOperator());
    }
  }
}
