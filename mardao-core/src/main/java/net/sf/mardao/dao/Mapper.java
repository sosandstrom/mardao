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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import net.sf.mardao.domain.AbstractEntityBuilder;

/**
 * Maps from Read to Domain to Write Key/Values.
 *
 * @author osandstrom Date: 2014-09-03 Time: 19:47
 */
public interface Mapper<T, ID extends Serializable> {
  ID fromKey(Object key);
  T fromReadValue(Object value);
  <RV> T fromReadValue(RV value, Supplier<Object, RV, ?, ?> supplier);
  String getCreatedByColumnName();
  String getCreatedDateColumnName();
  ID getId(T entity);
  Object getParentKey(T entity);
  String getKind();
  String getPrimaryKeyColumnName();
  String getParentKeyColumnName();
  String getUpdatedByColumnName();
  String getUpdatedDateColumnName();
  Object toKey(Object parentKey, ID id);
  void updateEntityPostWrite(T entity, Object key, Object value);
  void setParentKey(T entity, Object parentKey);
  void setPrimaryKey(Object writeValue, Object primaryKey);
  Object toWriteValue(T entity);
//  String getWriteSQL(Serializable id, Object writeValue, Collection arguments);
  Map<String, Class> getBasicFields();
//  void setValues(T entity, Object value);
}
