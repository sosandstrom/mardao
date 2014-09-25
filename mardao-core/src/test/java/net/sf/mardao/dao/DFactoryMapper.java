package net.sf.mardao.dao;

import java.util.Date;

import net.sf.mardao.domain.DFactory;
import net.sf.mardao.domain.DUser;

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
  public String getKind() {
    return DFactory.class.getSimpleName();
  }

  @Override
  public Object toKey(String sId) {
    return supplier.toKey(DFactory.class.getSimpleName(), sId);
  }

  @Override
  public Object toWriteValue(DFactory entity) {
    final String id = getId(entity);
    final Object key = toKey(id);
    final Object value = supplier.createWriteValue(key);
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
