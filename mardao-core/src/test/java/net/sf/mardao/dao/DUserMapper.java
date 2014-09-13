package net.sf.mardao.dao;

import net.sf.mardao.domain.DUser;

/**
 * To test AbstractDao.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:16
 */
public class DUserMapper implements Mapper<DUser, Long> {
  private static final String COLUMN_DISPLAYNAME = "displayName";
  private static final String COLUMN_ID = "id";
  private final Supplier supplier;

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
  public Object toKey(Long lId) {
    return supplier.toKey(DUser.class.getSimpleName(), lId);
  }

  @Override
  public Object toWriteValue(DUser entity) {
    final Long id = getId(entity);
    final Object key = toKey(id);
    final Object value = supplier.createWriteValue(key);
    supplier.setLong(value, COLUMN_ID, entity.getId());
    supplier.setString(value, COLUMN_DISPLAYNAME, entity.getDisplayName());
    return value;
  }

  @Override
  public DUser fromReadValue(Object core) {
    final DUser domain = new DUser();
    domain.setId(supplier.getLong(core, COLUMN_ID));
    domain.setDisplayName(supplier.getString(core, COLUMN_DISPLAYNAME));
    return domain;
  }
}
