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
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;

import net.sf.mardao.MappingIterable;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.KeyFuture;
import net.sf.mardao.core.EntityFuture;
import net.sf.mardao.core.filter.Filter;

import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;

/**
 * Generated Daos extends this base class.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:44
 */
public class AbstractDao<T, ID extends Serializable> implements CrudDao<T, ID> {

  /** set this, to have createdBy and updatedBy set */
  private static final ThreadLocal<String> principalName = new ThreadLocal<String>();
  private static final ThreadLocal<Stack> TRANSACTION_STACKS = new ThreadLocal<Stack>();

  private final Mapper<T, ID> mapper;
  private final Supplier supplier;

  protected AbstractDao(Mapper<T, ID> mapper, Supplier supplier) {
    this.mapper = mapper;
    this.supplier = supplier;
  }

  // --- transactional methods ---

  public <R> R withCommitTransaction(TransFunc<R> transFunc) throws IOException {
    return withTransaction(transFunc, true);
  }

  public <R> R withRollbackTransaction(TransFunc<R> transFunc) throws IOException {
    return withTransaction(transFunc, false);
  }

  public <R> R withTransaction(TransFunc<R> transFunc, boolean commit) throws IOException {
    return withTransaction(transFunc, commit, this.supplier);
  }

  public static <R> R withTransaction(TransFunc<R> transFunc, boolean commit, Supplier supplier) throws IOException {
    final Object transaction = supplier.beginTransaction();
    // TransactionHolder holder = new TransactionHolder(transaction, new Date());
    pushTransaction(transaction);
    try {
      final R result = transFunc.apply();
      if (commit) {
        supplier.commitTransaction(transaction);
      }
      return result;
    }
    finally {
      popTransaction(transaction);
      supplier.rollbackActiveTransaction(transaction);
    }
  }

  private static void pushTransaction(final Object transaction) {
    Stack stack = TRANSACTION_STACKS.get();
    if (null == stack) {
      stack = new Stack();
      TRANSACTION_STACKS.set(stack);
    }
    stack.push(transaction);
  }

  private static void popTransaction(final Object transaction) {
    final Stack stack = TRANSACTION_STACKS.get();
    Object popped = stack.pop();
    if (popped != transaction) {
      throw new IllegalStateException("Transaction differs.");
    }
  }

  private static Object getCurrentTransaction() {
    final Stack stack = TRANSACTION_STACKS.get();
    if (null == stack) {
      return null;
    }
    return stack.isEmpty() ? null : stack.peek();
  }

  // --- CRUD methods ---

  public int count() {
    return count(null);
  }

  @Override
  public int count(Object parentKey) {
    return supplier.count(getCurrentTransaction(), mapper.getKind(), parentKey, null);
  }

  public void delete(ID id) throws IOException {
    delete(null, id);
  }

  @Override
  public void delete(Object parentKey, ID id) throws IOException {
    Object key = mapper.toKey(parentKey, id);
    supplier.deleteValue(getCurrentTransaction(), key);
  }

  public void delete(Iterable<ID> ids) throws IOException {
    Collection<Object> keys = new ArrayList<Object>();
    for (ID id : ids) {
      keys.add(mapper.toKey(null, id));
    }
    supplier.deleteValues(getCurrentTransaction(), keys);
  }

  public T get(ID id) throws IOException {
    return get(null, id);
  }

  @Override
  public T get(Object parentKey, ID id) throws IOException {
    Object key = mapper.toKey(parentKey, id);
    Object value = supplier.readValue(getCurrentTransaction(), key);
    if (null == value) {
      return null;
    }
    T entity = mapper.fromReadValue(value);
    return entity;
  }

  @Override
  public ID put(Object parentKey, ID id, T entity) throws IOException {
    Object key = mapper.toKey(parentKey, id);
    Object value = mapper.toWriteValue(entity);
    updateAuditInfo(value);
    key = supplier.writeValue(getCurrentTransaction(), key, value);
    id = mapper.fromKey(key);
    mapper.updateEntityPostWrite(entity, key, value);
    return id;
  }

  public ID put(T entity) throws IOException {
    ID id = mapper.getId(entity);
    Object parentKey = mapper.getParentKey(entity);
    return put(parentKey, id, entity);
  }

  // --- query methods ---

  protected Iterable<T> queryByField(Object ancestorKey, String fieldName, Object fieldValue) {
    Iterable values = supplier.queryIterable(getCurrentTransaction(), mapper.getKind(), false, 0, -1,
      ancestorKey, null,
      null, false, null, false,
      Filter.equalsFilter(fieldName, fieldValue));
    return new MappingIterable<T, ID>(mapper, values.iterator());
  }

  protected T queryUniqueByField(Object parentKey, String fieldName, Object fieldValue) {
    final Object value = supplier.queryUnique(getCurrentTransaction(), parentKey, mapper.getKind(),
      Filter.equalsFilter(fieldName, fieldValue));
    if (null == value) {
      return null;
    }
    return mapper.fromReadValue(value);
  }

  public CursorPage<T> queryPage(int requestedPageSize, String cursorString) {
    return queryPage(null, requestedPageSize, cursorString);
  }

  @Override
  public CursorPage<T> queryPage(Object ancestorKey, int requestedPageSize, String cursorString) {
    return queryPage(false, requestedPageSize, ancestorKey,
        null, false, null, false, null, cursorString);
  }

  protected CursorPage<T> queryPage(boolean keysOnly, int requestedPageSize, Object ancestorKey,
                          String primaryOrderBy, boolean primaryIsAscending,
                          String secondaryOrderBy, boolean secondaryIsAscending,
                          Collection<String> projections,
                          String cursorString,
                          Filter... filters) {
    CursorPage page = supplier.queryPage(getCurrentTransaction(), mapper.getKind(), false,
      requestedPageSize, ancestorKey,
      primaryOrderBy, primaryIsAscending, secondaryOrderBy, secondaryIsAscending,
      projections, cursorString,
      filters);

    ArrayList<T> entities = new ArrayList<T>(page.getItems().size());
    for (Object value : page.getItems()) {
      T entity = mapper.fromReadValue(value);
      entities.add(entity);
    }
    page.setItems(entities);
    return page;
  }

  protected Iterable<T> queryIterable(boolean keysOnly, int offset, int limit, Object ancestorKey,
                                      String primaryOrderBy, boolean primaryIsAscending,
                                      String secondaryOrderBy, boolean secondaryIsAscending,
                                      Filter... filters) {

    final Iterable iterable = supplier.queryIterable(getCurrentTransaction(), mapper.getKind(), false,
            offset, limit, ancestorKey, null,
            primaryOrderBy, primaryIsAscending,
            secondaryOrderBy, secondaryIsAscending,
            filters);

    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {

        final Iterator wrappedIterator = iterable.iterator();
        return new Iterator<T>() {
          @Override
          public boolean hasNext() {
            return wrappedIterator.hasNext();
          }

          @Override
          public T next() {
            return mapper.fromReadValue(wrappedIterator.next());
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };

  }

  // --- async methods ---

  public Future<T> getAsync(Object parentKey, ID id) throws IOException {
    Object key = mapper.toKey(parentKey, id);
    Future<?> future = supplier.readFuture(getCurrentTransaction(), key);
    return new EntityFuture<T>(mapper, future);
  }

  public Future<ID> putAsync(T entity) throws IOException {
    ID id = mapper.getId(entity);
    Object parentKey = mapper.getParentKey(entity);
    Object key = mapper.toKey(parentKey, id);
    Object value = mapper.toWriteValue(entity);
    updateAuditInfo(value);
    Future<?> future = supplier.writeFuture(getCurrentTransaction(), key, value);
    return new KeyFuture(mapper, future, entity, value);
  }

  // --- utility methods ---

  public ID getId(Object key) {
    return mapper.fromKey(key);
  }

  public Object getKey(ID id) {
    return getKey(null, id);
  }

  public Object getKey(Object parentKey, ID id) {
    return mapper.toKey(parentKey, id);
  }

  public String getKind() {
    return mapper.getKind();
  }

  public void setParentKey(T entity, Object parentKey) {
    mapper.setParentKey(entity, parentKey);
  }

  public static void setPrincipalName(String name) {
    principalName.set(name);
  }

  private void updateAuditInfo(final Object value) {
    updateAuditInfo(value, principalName.get(), new Date(),
      mapper.getCreatedByColumnName(), mapper.getCreatedDateColumnName(),
      mapper.getUpdatedByColumnName(), mapper.getUpdatedDateColumnName());
  }

  protected void updateAuditInfo(final Object value, final String principalName, final Date date,
                               final String createdByColumnName, final String createdDateColumnName,
                               final String updatedByColumnName, final String updatedDateColumnName) {
    // createdBy
    if (null != createdByColumnName && null == supplier.getString(value, createdByColumnName)) {
      supplier.setString(value, createdByColumnName, principalName);
    }

    // createdDate
    if (null != createdDateColumnName && null == supplier.getDate(value, createdDateColumnName)) {
      supplier.setDate(value, createdDateColumnName, date);
    }

    // updatedBy
    if (null != updatedByColumnName && null != principalName) {
      supplier.setString(value, updatedByColumnName, principalName);
    }

    // updatedDate
    if (null != updatedDateColumnName && null != date) {
      supplier.setDate(value, updatedDateColumnName, date);
    }
  }


}
