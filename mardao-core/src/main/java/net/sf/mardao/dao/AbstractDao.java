package net.sf.mardao.dao;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

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
  //private static final ThreadLocal<Stack> TRANSACTION_STACKS = new ThreadLocal<Stack>();

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
    final Object transaction = supplier.beginTransaction();
    TransactionHolder holder = new TransactionHolder(transaction, new Date());
    // pushTransaction(transaction);
    try {
      final R result = transFunc.apply(holder);
      if (commit) {
        supplier.commitTransaction(holder);
      }
      return result;
    }
    finally {
      // popTransaction(transaction);
      supplier.rollbackActiveTransaction(holder);
    }
  }

//  private static void pushTransaction(final Object transaction) {
//    Stack stack = TRANSACTION_STACKS.get();
//    if (null == stack) {
//      stack = new Stack();
//      TRANSACTION_STACKS.set(stack);
//    }
//    stack.push(transaction);
//  }
//
//  private static void popTransaction(final Object transaction) {
//    final Stack stack = TRANSACTION_STACKS.get();
//    Object popped = stack.pop();
//    if (popped != transaction) {
//      throw new IllegalStateException("Transaction differs.");
//    }
//  }
//
//  private static Object getCurrentTransaction() {
//    final Stack stack = TRANSACTION_STACKS.get();
//    if (null == stack) {
//      return null;
//    }
//    return stack.peek();
//  }

  // --- CRUD methods ---

  public int count(TransactionHolder tx) {
    return supplier.count(tx, mapper.getKind(), null, null);
  }

  public void delete(TransactionHolder tx, ID id) throws IOException {
    Object key = mapper.toKey(id);
    supplier.deleteValue(tx, key);
  }

  public T get(TransactionHolder tx, ID id) throws IOException {
    Object key = mapper.toKey(id);
    Object value = supplier.readValue(tx, key);
    if (null == value) {
      return null;
    }
    T entity = mapper.fromReadValue(value);
    return entity;
  }

  public ID put(TransactionHolder tx, T entity) throws IOException {
    ID id = mapper.getId(entity);
    Object key = mapper.toKey(id);
    Object value = mapper.toWriteValue(entity);
    updateAuditInfo(tx, value);
    key = supplier.writeValue(tx, key, value);
    id = mapper.fromKey(key);
    return id;
  }

  // --- query methods ---

  protected Iterable<T> queryByField(TransactionHolder tx, String fieldName, Object fieldValue) {
    Iterable values = supplier.queryIterable(tx, mapper.getKind(), false, 0, -1,
      null, null,
      null, false, null, false,
      Filter.equalsFilter(fieldName, fieldValue));
    return new MappingIterable<T, ID>(mapper, values.iterator());
  }

  protected T queryUniqueByField(TransactionHolder tx, String fieldName, Object fieldValue) {
    final Object value = supplier.queryUnique(tx, mapper.getKind(), Filter.equalsFilter(fieldName, fieldValue));
    if (null == value) {
      return null;
    }
    return mapper.fromReadValue(value);
  }

  // --- utility methods ---

  public ID getId(Object key) {
    return mapper.fromKey(key);
  }

  public static void setPrincipalName(String name) {
    principalName.set(name);
  }

  private void updateAuditInfo(TransactionHolder tx, final Object value) {
    updateAuditInfo(value, principalName.get(), tx.getDate(),
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
