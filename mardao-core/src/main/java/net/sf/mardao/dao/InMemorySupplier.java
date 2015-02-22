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

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Stores entities in-memory using a new TreeMap&lt;InMemoryKey, Map&lt;String, Object&gt;&gt;.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:48
 */
public class InMemorySupplier extends AbstractSupplier<InMemoryKey, Map<String, Object>, Map<String, Object>, Object> {

  public static final String NAME_PARENT_KEY = "__parentKey";
  public static final String NAME_KEY = "__Key";

  private final Map<String, Map<String, Map<String, Object>>> store = new TreeMap<String, Map<String, Map<String, Object>>>();

  @Override
  public Object beginTransaction() {
    return new Object();
  }

  @Override
  public void commitTransaction(Object tx) {

  }

  @Override
  public void rollbackActiveTransaction(Object tx) {

  }

  @Override
  public int count(Object tx, Mapper mapper, InMemoryKey ancestorKey, InMemoryKey simpleKey, Filter... filters) {
    final Collection<Map<String, Object>> filtered = filterValues(kindStore(mapper.getKind()).values());
    return filtered.size();
  }

  @Override
  public void deleteValue(Object tx, InMemoryKey key) throws IOException {
    kindStore(key).remove(key.getName());
  }

  @Override
  public void deleteValues(Object tx, Collection<InMemoryKey> keys) throws IOException {
    for (InMemoryKey key : keys) {
      deleteValue(tx, key);
    }
  }

  @Override
  public Map<String, Object> readValue(Object tx, Mapper mapper, InMemoryKey key) throws IOException {
    return kindStore(key).get(key.getName());
  }

  @Override
  public Future<Map<String, Object>> readFuture(final Object tx, final Mapper mapper, final InMemoryKey key) throws IOException {
    FutureTask<Map<String, Object>> task = new FutureTask<Map<String, Object>>(new Callable<Map<String, Object>>() {
      @Override
      public Map<String, Object> call() throws Exception {
        return readValue(tx, mapper, key);
      }
    });
    new Thread(task).start();
    return task;
  }

  @Override
  public Future<InMemoryKey> writeFuture(final Object tx, final InMemoryKey key, final Map<String, Object> value) throws IOException {
    FutureTask<InMemoryKey> task = new FutureTask<InMemoryKey>(new Callable<InMemoryKey>() {
      @Override
      public InMemoryKey call() throws Exception {
        return writeValue(tx, key, value);
      }
    });
    new Thread(task).start();
    return task;
  }

  @Override
  public Collection getCollection(Map<String, Object> value, String column) {
    return (Collection) value.get(column);
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
  public InMemoryKey getKey(Map<String, Object> value, String column) {
    return (InMemoryKey) value.get(NAME_KEY);
  }

  @Override
  public InMemoryKey getParentKey(Map<String, Object> value, String column) {
    return (InMemoryKey) value.get(NAME_PARENT_KEY);
  }

  @Override
  public String getString(Map<String, Object> core, String column) {
    return (String) core.get(column);
  }

  @Override
  public Integer getInteger(Map<String, Object> core, String column) {
    return (Integer) core.get(column);
  }

  @Override
  public Boolean getBoolean(Map<String, Object> core, String column) {
    return (Boolean) core.get(column);
  }

  @Override
  public Float getFloat(Map<String, Object> core, String column) {
    return (Float) core.get(column);
  }

  @Override
  public ByteBuffer getByteBuffer(Map<String, Object> core, String column) {
    return (ByteBuffer) core.get(column);
  }

  @Override
  protected void setObject(Map<String, Object> value, String column, Object o) {
    value.put(column, o);
  }

  @Override
  public Map<String, Object> createWriteValue(Mapper mapper, InMemoryKey parentKey, Long id, Object entity) {
    return createWriteValue(parentKey, toKey(parentKey, mapper.getKind(), id));
  }

  private Map<String, Object> createWriteValue(InMemoryKey parentKey, InMemoryKey key) {
    final TreeMap<String, Object> value = new TreeMap<String, Object>();
    value.put(NAME_KEY, key);
    value.put(NAME_PARENT_KEY, parentKey);
    return value;
  }

  @Override
  public Map<String, Object> createWriteValue(Mapper mapper, InMemoryKey parentKey, String id, Object entity) {
    return createWriteValue(parentKey, toKey(parentKey, mapper.getKind(), id));
  }

  @Override
  public Iterable<Map<String, Object>> queryIterable(Object tx, String kind, boolean keysOnly, int offset, int limit,
                                                     InMemoryKey ancestorKey, InMemoryKey simpleKey,
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
  public Map<String, Object> queryUnique(Object tx, InMemoryKey parentKey, String kind, Filter... filters) {
    final Iterable<Map<String, Object>> iterable = queryIterable(tx, kind, false, 0, 1,
      parentKey, null, null, false, null, false, filters);
    final Iterator<Map<String, Object>> iterator = iterable.iterator();
    return iterator.hasNext() ? iterator.next() : null;
  }

  @Override
  public CursorPage<Map<String, Object>> queryPage(Object tx, Mapper mapper, boolean keysOnly,
                                                   int requestedPageSize, InMemoryKey ancestorKey,
                                                   String primaryOrderBy, boolean primaryIsAscending,
                                                   String secondaryOrderBy, boolean secondaryIsAscending,
                                                   Collection<String> projections, String cursorString,
                                                   Filter... filters) {
    CursorPage<Map<String, Object>> page = new CursorPage<Map<String, Object>>();
    Collection<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
    page.setItems(values);
    if (null == cursorString) {
      page.setTotalSize(filterValues(kindStore(mapper.getKind()).values(), filters).size());
    }

    boolean foundCursor = null == cursorString;
    for (Map.Entry<String, Map<String, Object>> entry : kindStore(mapper.getKind()).entrySet()) {
      if (!foundCursor) {
        foundCursor = entry.getKey().toString().equals(cursorString);
      }
      else if (matchAll(entry.getValue(), filters)) {
        values.add(entry.getValue());
        if (requestedPageSize == values.size()) {
          page.setCursorKey(entry.getKey().toString());
          break;
        }
      }
    }
    return page;
  }

  @Override
  public InMemoryKey writeValue(Object tx, InMemoryKey key, Map<String, Object> core) throws IOException {
    // assign long key?
    if (null == key.getName()) {
      key = InMemoryKey.of(key.getParentKey(), key.getKind(), Long.toString(Math.round(Math.random() * Long.MAX_VALUE)));
    }
    kindStore(key).put(key.getName(), core);
    return key;
  }

  @Override
  public InMemoryKey toKey(InMemoryKey parentKey, String kind, Long lId) {
    return InMemoryKey.of(parentKey, kind, null != lId ? lId.toString() : null);
  }

  @Override
  public InMemoryKey toKey(InMemoryKey parentKey, String kind, String sId) {
    return InMemoryKey.of(parentKey, kind, sId);
  }

  @Override
  public Long toLongKey(InMemoryKey key) {
    return null != key ? Long.parseLong(key.getName()) : null;
  }

  @Override
  public String toStringKey(InMemoryKey key) {
    return null != key ? key.getName() : null;
  }

  @Override
  public InMemoryKey toParentKey(InMemoryKey key) {
    return null != key ? key.getParentKey() : null;
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

  protected static boolean matchAll(Map<String, Object> v, Filter... filters) {
    if (null == filters) {
      return true;
    }
    for (Filter f : filters) {
      if (!match(v, f)) {
        return false;
      }
    }
    return true;
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
