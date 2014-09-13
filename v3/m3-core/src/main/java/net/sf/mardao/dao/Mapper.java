package net.sf.mardao.dao;

import java.io.Serializable;

/**
 * Maps from Read to Domain to Write Key/Values.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:47
 */
public interface Mapper<T, ID extends Serializable> {
  ID fromKey(Object key);
  T fromReadValue(Object value);
  ID getId(T entity);
  Object toKey(ID id);
  Object toWriteValue(T entity);
}
