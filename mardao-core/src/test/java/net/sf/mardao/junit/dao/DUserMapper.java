package net.sf.mardao.junit.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

import net.sf.mardao.core.ColumnField;
import net.sf.mardao.core.CreatedBy;
import net.sf.mardao.core.CreatedDate;
import net.sf.mardao.dao.Mapper;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.domain.AbstractEntityBuilder;
import net.sf.mardao.junit.domain.DUser;

import javax.persistence.Basic;
import javax.persistence.Id;

/**
 * The DUser domain-object specific mapping methods go here.
 *
 * Generated on 2015-02-28T10:34:05.595+0100.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public class DUserMapper
  implements Mapper<DUser, Long> {

  private final Supplier supplier;

  public enum Field {
    ID("id"),
    BIRTHDATE("birthDate"),
    CREATEDBY("createdBy"),
    DISPLAYNAME("displayName"),
    EMAIL("email");

    private final String fieldName;

    Field(String fieldName) {
      this.fieldName = fieldName;
    }

    public String getFieldName() {
      return fieldName;
    }
  }

    @Override
    public Map<String, ColumnField> getBasicFields() {
        TreeMap<String,ColumnField> map = new TreeMap<String,ColumnField>();
        map.put(Field.DISPLAYNAME.getFieldName(),
                new ColumnField(Field.DISPLAYNAME.getFieldName(), String.class, Basic.class));
        map.put(Field.EMAIL.getFieldName(),
                new ColumnField(Field.EMAIL.getFieldName(), String.class, Basic.class));
        return map;
    }

    @Override
    public Map<Class, ColumnField> getSpecialFields() {
        Map<Class, ColumnField> map = new TreeMap<Class, ColumnField>();
        map.put(Id.class,
                new ColumnField(Field.ID.getFieldName(), Long.class, Id.class));
        map.put(CreatedBy.class,
                new ColumnField(Field.CREATEDBY.getFieldName(), String.class, CreatedBy.class));
        map.put(CreatedDate.class,
                new ColumnField(Field.BIRTHDATE.getFieldName(), Date.class, CreatedDate.class));
        return map;
    }

    public DUserMapper(Supplier supplier) {
    this.supplier = supplier;
  }

  @Override
  public Long fromKey(Object key) {
    return supplier.toLongKey(key);
  }

  @Override
  public DUser fromReadValue(Object value) {
    return fromReadValue(value, supplier);
  }

  @Override
  public <RV> DUser fromReadValue(RV value, Supplier<Object, RV, ?, ?> specificSupplier) {
    DUser entity = (DUser) specificSupplier.createEntity(this, value);
    if (null != entity) {
      return entity;
    }

    entity = new DUser();

    // set primary key:
    final Object key = specificSupplier.getKey(value, Field.ID.getFieldName());
    entity.setId(specificSupplier.toLongKey(key));

    // set all fields:
    entity.setBirthDate(specificSupplier.getDate(value, Field.BIRTHDATE.getFieldName()));
    entity.setCreatedBy(specificSupplier.getString(value, Field.CREATEDBY.getFieldName()));
    entity.setDisplayName(specificSupplier.getString(value, Field.DISPLAYNAME.getFieldName()));
    entity.setEmail(specificSupplier.getString(value, Field.EMAIL.getFieldName()));
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
  public String getPrimaryKeyColumnName() {
    return Field.ID.getFieldName();
  }

  @Override
  public String getParentKeyColumnName() {
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

  @Override
  public Long getId(DUser entity) {
    return entity != null ? entity.getId() : null;
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
  public void setPrimaryKey(Object writeValue, Object primaryKey) {
    supplier.setLong(writeValue, Field.ID.getFieldName(), supplier.toLongKey(primaryKey));
  }

  @Override
  public void updateEntityPostWrite(DUser entity, Object key, Object value) {
    entity.setId(supplier.toLongKey(key));
    entity.setCreatedBy(supplier.getWriteString(value, Field.CREATEDBY.getFieldName()));
    entity.setBirthDate(supplier.getWriteDate(value, Field.BIRTHDATE.getFieldName()));
}

@Override
  public String getKind() {
    return DUser.class.getSimpleName();
  }

  @Override
  public Object toKey(Object parentKey, Long id) {
    return supplier.toKey(parentKey, getKind(), id);
  }

  @Override
  public Object toWriteValue(DUser entity) {
    final Long id = getId(entity);
    final Object parentKey = getParentKey(entity);
    final Object value = supplier.createWriteValue(this, parentKey, id, entity);
    // some suppliers cannot set the keys in above method
    supplier.setPrimaryKey(value, this, Field.ID.getFieldName(), toKey(parentKey, id), entity);

    // set all fields:
    supplier.setDate(value, Field.BIRTHDATE.getFieldName(), entity.getBirthDate());
    supplier.setString(value, Field.CREATEDBY.getFieldName(), entity.getCreatedBy());
    supplier.setString(value, Field.DISPLAYNAME.getFieldName(), entity.getDisplayName());
    supplier.setString(value, Field.EMAIL.getFieldName(), entity.getEmail());
    return value;
  }


  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends AbstractEntityBuilder<DUser> {

    protected Builder() {
      super(new DUser());
    }

    public Builder id(Long id) {
      entity.setId(id);
      return this;
    }

    public Builder birthDate(Date birthDate) {
      entity.setBirthDate(birthDate);
      return this;
    }

    public Builder createdBy(String createdBy) {
      entity.setCreatedBy(createdBy);
      return this;
    }

    public Builder displayName(String displayName) {
      entity.setDisplayName(displayName);
      return this;
    }

    public Builder email(String email) {
      entity.setEmail(email);
      return this;
    }

  }
}
