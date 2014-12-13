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

import net.sf.mardao.domain.DFactory;

/**
 * To test AbstractDao.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:16
 */
public class DFactoryMapper implements Mapper<DFactory, String> {
  private static final String COLUMN_PROVIDERID = "providerId";
  private final Supplier supplier;

  public DFactoryMapper(Supplier supplier) {
    this.supplier = supplier;
  }

  @Override
  public String fromKey(Object key) {
    return supplier.toStringKey(key);
  }

  @Override
  public String getId(DFactory entity) {
    return null != entity ? entity.getProviderId() : null;
  }

  @Override
  public Object getParentKey(DFactory entity) {
    return null;
  }

  @Override
  public void updateEntityPostWrite(DFactory entity, Object key, Object value) {
    entity.setProviderId(supplier.toStringKey(key));
  }

  @Override
  public void setParentKey(DFactory entity, Object parentKey) {
    // this entity has no parent
  }

  @Override
  public String getKind() {
    return DFactory.class.getSimpleName();
  }

  @Override
  public Object toKey(Object parentKey, String sId) {
    return supplier.toKey(parentKey, DFactory.class.getSimpleName(), sId);
  }

  @Override
  public Object toWriteValue(DFactory entity) {
    final String id = getId(entity);
    final Object parentKey = getParentKey(entity);
    final Object value = supplier.createWriteValue(parentKey, getKind(), id);
    supplier.setString(value, COLUMN_PROVIDERID, entity.getProviderId());
    return value;
  }

  @Override
  public DFactory fromReadValue(Object core) {
    final DFactory domain = new DFactory();
    domain.setProviderId(supplier.getString(core, COLUMN_PROVIDERID));
    return domain;
  }

  @Override
  public String getCreatedByColumnName() {
    return null;
  }

  @Override
  public String getCreatedDateColumnName() {
    return null;
  }

  @Override
  public String getUpdatedByColumnName() {
    return null;
  }

  @Override
  public String getUpdatedDateColumnName() {
    return null;
  }
}
