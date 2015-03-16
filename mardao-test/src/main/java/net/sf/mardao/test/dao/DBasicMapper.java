package net.sf.mardao.test.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.nio.ByteBuffer;

import javax.persistence.Basic;
import javax.persistence.Id;

import net.sf.mardao.core.ColumnField;
import net.sf.mardao.core.CreatedBy;
import net.sf.mardao.core.CreatedDate;
import net.sf.mardao.core.Parent;
import net.sf.mardao.core.UpdatedBy;
import net.sf.mardao.core.UpdatedDate;
import net.sf.mardao.dao.Mapper;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.domain.AbstractEntityBuilder;
import net.sf.mardao.test.domain.DBasic;

/**
 * The DBasic domain-object specific mapping methods go here.
 *
 * Generated on 2015-03-16T19:30:26.611+0100.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public class DBasicMapper
  implements Mapper<DBasic, Long> {

  private final Supplier supplier;

  public enum Field {
    ID("id"),
    CREATEDBY("createdBy"),
    CREATEDDATE("createdDate"),
    DISPLAYNAME("displayName"),
    UPDATEDBY("updatedBy"),
    UPDATEDDATE("updatedDate");

    private final String fieldName;

    Field(String fieldName) {
      this.fieldName = fieldName;
    }

    public String getFieldName() {
      return fieldName;
    }
  }

  private static final Map<String, ColumnField> BASIC_FIELDS = new TreeMap<String, ColumnField>();
  private static final Map<Class, ColumnField> SPECIAL_FIELDS = new HashMap<Class, ColumnField>();
  private static final Map<String, ColumnField> ALL_FIELDS = new TreeMap<String, ColumnField>();

  static {
    SPECIAL_FIELDS.put(Id.class,
        new ColumnField(Field.ID.getFieldName(), Long.class, Id.class));
    SPECIAL_FIELDS.put(CreatedBy.class,
        new ColumnField(Field.CREATEDBY.getFieldName(), String.class, CreatedBy.class));
    SPECIAL_FIELDS.put(CreatedDate.class,
        new ColumnField(Field.CREATEDDATE.getFieldName(), Date.class, CreatedDate.class));
    BASIC_FIELDS.put(Field.DISPLAYNAME.getFieldName(),
        new ColumnField(Field.DISPLAYNAME.getFieldName(), String.class, Basic.class));
    SPECIAL_FIELDS.put(UpdatedBy.class,
        new ColumnField(Field.UPDATEDBY.getFieldName(), String.class, UpdatedBy.class));
    SPECIAL_FIELDS.put(UpdatedDate.class,
        new ColumnField(Field.UPDATEDDATE.getFieldName(), Date.class, UpdatedDate.class));

    ALL_FIELDS.putAll(BASIC_FIELDS);
    for (ColumnField sf : SPECIAL_FIELDS.values()) {
        ALL_FIELDS.put(sf.getColumnName(), sf);
    }
  }

  @Override
  public Map<String, ColumnField> getBasicFields() {
    return BASIC_FIELDS;
  }

  @Override
  public Map<Class, ColumnField> getSpecialFields() {
    return SPECIAL_FIELDS;
  }

  public DBasicMapper(Supplier supplier) {
    this.supplier = supplier;
  }

  @Override
  public Long fromKey(Object key) {
    return supplier.toLongKey(key);
  }

  @Override
  public DBasic fromReadValue(Object value) {
    return fromReadValue(value, supplier);
  }

  @Override
  public <RV> DBasic fromReadValue(RV value, Supplier<Object, RV, ?, ?> specificSupplier) {
    DBasic entity = (DBasic) specificSupplier.createEntity(this, value);
    if (null != entity) {
      return entity;
    }

    entity = new DBasic();

    // set primary key:
    final Object key = specificSupplier.getKey(value, Field.ID.getFieldName());
    entity.setId(specificSupplier.toLongKey(key));

    // set all fields:
    entity.setCreatedBy(specificSupplier.getString(value, Field.CREATEDBY.getFieldName()));
    entity.setCreatedDate(specificSupplier.getDate(value, Field.CREATEDDATE.getFieldName()));
    entity.setDisplayName(specificSupplier.getString(value, Field.DISPLAYNAME.getFieldName()));
    entity.setUpdatedBy(specificSupplier.getString(value, Field.UPDATEDBY.getFieldName()));
    entity.setUpdatedDate(specificSupplier.getDate(value, Field.UPDATEDDATE.getFieldName()));
    return entity;
  }

    public Field getCreatedByField() {
    return Field.CREATEDBY;
    }

  @Override
  public String getCreatedByColumnName() {
    return Field.CREATEDBY.getFieldName();
  }

    public Field getCreatedDateField() {
    return Field.CREATEDDATE;
    }

  @Override
  public String getCreatedDateColumnName() {
    return Field.CREATEDDATE.getFieldName();
  }

  public Field getPrimaryKeyField() {
    return Field.ID;
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
    return Field.UPDATEDBY.getFieldName();
  }

  @Override
  public String getUpdatedDateColumnName() {
    return Field.UPDATEDDATE.getFieldName();
  }

  @Override
  public Long getId(DBasic entity) {
    return entity != null ? entity.getId() : null;
  }

  @Override
  public Object getParentKey(DBasic entity) {
    return null;
  }

  @Override
  public void setParentKey(DBasic entity, Object parentKey) {
    // this entity has no parent
  }

  @Override
  public void setPrimaryKey(Object writeValue, Object primaryKey) {
    supplier.setLong(writeValue, Field.ID.getFieldName(), supplier.toLongKey(primaryKey));
  }

  @Override
  public void updateEntityPostWrite(DBasic entity, Object key, Object value) {
    entity.setId(supplier.toLongKey(key));
    entity.setCreatedBy(supplier.getWriteString(value, Field.CREATEDBY.getFieldName()));
    entity.setCreatedDate(supplier.getWriteDate(value, Field.CREATEDDATE.getFieldName()));
    entity.setUpdatedBy(supplier.getWriteString(value, Field.UPDATEDBY.getFieldName()));
    entity.setUpdatedDate(supplier.getWriteDate(value, Field.UPDATEDDATE.getFieldName()));
}

@Override
  public String getKind() {
    return DBasic.class.getSimpleName();
  }

  @Override
  public Object toKey(Object parentKey, Long id) {
    return supplier.toKey(parentKey, getKind(), id);
  }

  @Override
  public Object toWriteValue(DBasic entity) {
    final Long id = getId(entity);
    final Object parentKey = getParentKey(entity);
    final Object value = supplier.createWriteValue(this, parentKey, id, entity);
    // some suppliers cannot set the keys in above method
    supplier.setPrimaryKey(value, this, Field.ID.getFieldName(), toKey(parentKey, id), entity);

    // set all fields:
    supplier.setString(value, Field.CREATEDBY.getFieldName(), entity.getCreatedBy());
    supplier.setDate(value, Field.CREATEDDATE.getFieldName(), entity.getCreatedDate());
    supplier.setString(value, Field.DISPLAYNAME.getFieldName(), entity.getDisplayName());
    supplier.setString(value, Field.UPDATEDBY.getFieldName(), entity.getUpdatedBy());
    supplier.setDate(value, Field.UPDATEDDATE.getFieldName(), entity.getUpdatedDate());
    return value;
  }

//  @Override
  public String getWriteSQL(Serializable id, Object writeValue, Collection arguments) {
    final StringBuilder sql = new StringBuilder("UPDATE ")
        .append(getKind())
        .append(" SET ");

    boolean first = true;
    for (Field f : Field.values()) {
        if (!getPrimaryKeyField().equals(f) &&
            !getCreatedByField().equals(f) &&
            !getCreatedDateField().equals(f)) {
           if (first) {
                first = false;
           }
           else {
                sql.append(", ");
           }
           sql.append(f.getFieldName())
            .append("=?");
           if (null != arguments && null != writeValue) {
            Object arg = supplier.getWriteObject(writeValue, f.getFieldName());
            arguments.add(arg);
           }
        }
    }

    sql.append(" WHERE ")
        .append(getPrimaryKeyColumnName())
        .append("=?");
    if (null != arguments) {
        arguments.add(id);
    }
    return sql.toString();
  }


  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends AbstractEntityBuilder<DBasic> {

    protected Builder() {
      super(new DBasic());
    }

    public Builder id(Long id) {
      entity.setId(id);
      return this;
    }

    public Builder createdBy(String createdBy) {
      entity.setCreatedBy(createdBy);
      return this;
    }

    public Builder createdDate(Date createdDate) {
      entity.setCreatedDate(createdDate);
      return this;
    }

    public Builder displayName(String displayName) {
      entity.setDisplayName(displayName);
      return this;
    }

    public Builder updatedBy(String updatedBy) {
      entity.setUpdatedBy(updatedBy);
      return this;
    }

    public Builder updatedDate(Date updatedDate) {
      entity.setUpdatedDate(updatedDate);
      return this;
    }

  }
}
