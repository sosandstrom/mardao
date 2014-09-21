package net.sf.mardao.dao;

import net.sf.mardao.domain.DUser;

/**
 * To test AbstractDao.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:11
 */
public class DUserDao extends AbstractDao<DUser, Long> {
  public DUserDao(Supplier supplier) {
    super(new DUserMapper(supplier), supplier);
  }

  public Iterable<DUser> queryByDisplayName(String displayName) {
    return queryByField(DUserMapper.Field.DISPLAYNAME.getFieldName(), displayName);
  }
}
