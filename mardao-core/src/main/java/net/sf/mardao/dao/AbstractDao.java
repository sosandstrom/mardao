package net.sf.mardao.dao;

import java.io.IOException;
import java.io.Serializable;

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

  private final Mapper<T, ID> mapper;
  private final Supplier supplier;

  protected AbstractDao(Mapper<T, ID> mapper, Supplier supplier) {
    this.mapper = mapper;
    this.supplier = supplier;
  }

  // --- CRUD methods ---

  public int count() {
    return supplier.count(mapper.getKind(), null, null);
  }

  public void delete(ID id) throws IOException {
    Object key = mapper.toKey(id);
    supplier.deleteValue(key);
  }

  public T get(ID id) throws IOException {
    Object key = mapper.toKey(id);
    Object value = supplier.readValue(key);
    if (null == value) {
      return null;
    }
    T entity = mapper.fromReadValue(value);
    return entity;
  }

  public ID put(T entity) throws IOException {
    ID id = mapper.getId(entity);
    Object key = mapper.toKey(id);
    Object value = mapper.toWriteValue(entity);
    key = supplier.writeValue(key, value);
    id = mapper.fromKey(key);
    return id;
  }

  // --- query methods ---

  protected Iterable<T> queryByField(String fieldName, Object fieldValue) {
    Iterable values = supplier.queryIterable(mapper.getKind(), false, 0, -1,
      null, null,
      null, false, null, false,
      Filter.equalsFilter(fieldName, fieldValue));
    return new MappingIterable<T, ID>(mapper, values.iterator());
  }

  protected T queryUniqueByField(String fieldName, Object fieldValue) {
    final Object value = supplier.queryUnique(mapper.getKind(), Filter.equalsFilter(fieldName, fieldValue));
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
}
