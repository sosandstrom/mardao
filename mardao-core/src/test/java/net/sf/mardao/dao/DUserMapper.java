package net.sf.mardao.dao;

import java.util.Date;

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
    final Object key = toKey(parentKey, id);
    final Object value = supplier.createWriteValue(key);
    supplier.setLong(value, Field.ID.getFieldName(), entity.getId());
    supplier.setString(value, Field.DISPLAYNAME.getFieldName(), entity.getDisplayName());
    supplier.setString(value, Field.EMAIL.getFieldName(), entity.getEmail());
    supplier.setString(value, Field.CREATEDBY.getFieldName(), entity.getCreatedBy());
    supplier.setDate(value, Field.BIRTHDATE.getFieldName(), entity.getBirthDate());
    return value;
  }

  @Override
  public DUser fromReadValue(Object core) {
    final DUser domain = new DUser();
    domain.setId(supplier.getLong(core, Field.ID.getFieldName()));
    domain.setDisplayName(supplier.getString(core, Field.DISPLAYNAME.getFieldName()));
    domain.setEmail(supplier.getString(core, Field.EMAIL.getFieldName()));
    domain.setCreatedBy(supplier.getString(core, Field.CREATEDBY.getFieldName()));
    domain.setBirthDate(supplier.getDate(core, Field.BIRTHDATE.getFieldName()));
    return domain;
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
}
