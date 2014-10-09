package net.sf.mardao.dao;

import java.io.Serializable;
import java.util.Date;

import net.sf.mardao.domain.AbstractEntityBuilder;

/**
 * Maps from Read to Domain to Write Key/Values.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:47
 */
public interface Mapper<T, ID extends Serializable> {
  ID fromKey(Object key);
  T fromReadValue(Object value);
  String getCreatedByColumnName();
  String getCreatedDateColumnName();
  ID getId(T entity);
  Object getParentKey(T entity);
  String getKind();
  String getUpdatedByColumnName();
  String getUpdatedDateColumnName();
  Object toKey(Object parentKey, ID id);
  void updateEntityPostWrite(T entity, Object key, Object value);
  void setParentKey(T entity, Object parentKey);
  Object toWriteValue(T entity);
}
