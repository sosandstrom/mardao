package net.sf.mardao.dao;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Stack;

import net.sf.mardao.MappingIterable;
import net.sf.mardao.core.filter.Filter;

/**
 * Generated Daos extends this base class.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:44
 */
public class AbstractDao<T, ID extends Serializable> {

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

  public int count(Object parentKey) {
    return supplier.count(getCurrentTransaction(), mapper.getKind(), parentKey, null);
  }

  public void delete(ID id) throws IOException {
    delete(null, id);
  }

  public void delete(Object parentKey, ID id) throws IOException {
    Object key = mapper.toKey(parentKey, id);
    supplier.deleteValue(getCurrentTransaction(), key);
  }

  public T get(ID id) throws IOException {
    return get(null, id);
  }

  public T get(Object parentKey, ID id) throws IOException {
    Object key = mapper.toKey(parentKey, id);
    Object value = supplier.readValue(getCurrentTransaction(), key);
    if (null == value) {
      return null;
    }
    T entity = mapper.fromReadValue(value);
    return entity;
  }

  public ID put(T entity) throws IOException {
    ID id = mapper.getId(entity);
    Object parentKey = mapper.getParentKey(entity);
    Object key = mapper.toKey(parentKey, id);
    Object value = mapper.toWriteValue(entity);
    updateAuditInfo(value);
    key = supplier.writeValue(getCurrentTransaction(), key, value);
    id = mapper.fromKey(key);
    mapper.updateEntityPostWrite(entity, key, value);
    return id;
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
