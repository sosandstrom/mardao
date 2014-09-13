package net.sf.mardao.dao;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:44
 */
public class AbstractDao<T, ID extends Serializable> {
  private final Mapper<T, ID> mapper;
  private final Supplier supplier;

  protected AbstractDao(Mapper<T, ID> mapper, Supplier supplier) {
    this.mapper = mapper;
    this.supplier = supplier;
  }

  public T get(ID id) throws IOException {
    Object key = mapper.toKey(id);
    Object value = supplier.readValue(key);
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
}
