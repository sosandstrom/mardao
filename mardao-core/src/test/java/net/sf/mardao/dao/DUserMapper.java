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

import java.util.Date;

import net.sf.mardao.domain.AbstractEntityBuilder;
import net.sf.mardao.domain.DUser;

/**
 * To test AbstractDao.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:16
 */
public class DUserMapper implements Mapper<DUser, Long> {
  private final Supplier supplier;

  public enum Field {
    ID("id"),
    EMAIL("email"),
    BIRTHDATE("birthDate"),
    CREATEDBY("createdBy"),
    DISPLAYNAME("displayName");

    private final String fieldName;

    Field(String fieldName) {
      this.fieldName = fieldName;
    }

    public String getFieldName() {
      return fieldName;
    }
  }

  public DUserMapper(Supplier supplier) {
    this.supplier = supplier;
  }

  @Override
  public Long fromKey(Object key) {
    return supplier.toLongKey(key);
  }

  @Override
  public Long getId(DUser entity) {
    return null != entity ? entity.getId() : null;
  }

  @Override
  public Object getParentKey(DUser entity) {
    return null;
  }

  @Override
  public void setParentKey(DUser entity, Object parentKey) {
    // this entity has no parent
  }

  @Override
  public void updateEntityPostWrite(DUser entity, Object key, Object value) {
    entity.setId(supplier.toLongKey(key));
    entity.setCreatedBy(supplier.getString(value, Field.CREATEDBY.getFieldName()));
    entity.setBirthDate(supplier.getDate(value, Field.BIRTHDATE.getFieldName()));
  }

  @Override
  public String getKind() {
    return DUser.class.getSimpleName();
  }

  @Override
  public Object toKey(Object parentKey, Long lId) {
    return supplier.toKey(parentKey, DUser.class.getSimpleName(), lId);
  }

  @Override
  public Object toWriteValue(DUser entity) {
    final Long id = getId(entity);
    final Object parentKey = getParentKey(entity);
    final Object value = supplier.createWriteValue(parentKey, getKind(), id);
    supplier.setLong(value, Field.ID.getFieldName(), entity.getId());
    supplier.setString(value, Field.DISPLAYNAME.getFieldName(), entity.getDisplayName());
    supplier.setString(value, Field.EMAIL.getFieldName(), entity.getEmail());
    supplier.setString(value, Field.CREATEDBY.getFieldName(), entity.getCreatedBy());
    supplier.setDate(value, Field.BIRTHDATE.getFieldName(), entity.getBirthDate());
    return value;
  }

  @Override
  public DUser fromReadValue(Object core) {
    final DUser entity = new DUser();
    entity.setId(supplier.getLong(core, Field.ID.getFieldName()));
    entity.setDisplayName(supplier.getString(core, Field.DISPLAYNAME.getFieldName()));
    entity.setEmail(supplier.getString(core, Field.EMAIL.getFieldName()));
    entity.setCreatedBy(supplier.getString(core, Field.CREATEDBY.getFieldName()));
    entity.setBirthDate(supplier.getDate(core, Field.BIRTHDATE.getFieldName()));
    return entity;
  }

  @Override
  public String getCreatedByColumnName() {
    return Field.CREATEDBY.getFieldName();
  }

  @Override
  public String getCreatedDateColumnName() {
    return Field.BIRTHDATE.getFieldName();
  }

  @Override
  public String getUpdatedByColumnName() {
    return null;
  }

  @Override
  public String getUpdatedDateColumnName() {
    return null;
  }

  public static DUserBuilder newBuilder() {
    return new DUserBuilder();
  }

  public static class DUserBuilder extends AbstractEntityBuilder<DUser> {

    @Override
    protected DUser newInstance() {
      return new DUser();
    }

    public DUserBuilder id(Long id) {
      entity.setId(id);
      return this;
    }

    public DUserBuilder displayName(String displayName) {
      entity.setDisplayName(displayName);
      return this;
    }

    public DUserBuilder email(String email) {
      entity.setEmail(email);
      return this;
    }

    public DUserBuilder birthDate(Date birthDate) {
      entity.setBirthDate(birthDate);
      return this;
    }
  }
}
