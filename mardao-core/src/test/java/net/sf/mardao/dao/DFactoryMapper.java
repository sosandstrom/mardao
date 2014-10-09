package net.sf.mardao.dao;

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
