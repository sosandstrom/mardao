package net.sf.mardao.dao;

import net.sf.mardao.domain.DFactory;
import net.sf.mardao.domain.DUser;

/**
 * To test AbstractDao.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:11
 */
public class DFactoryDao extends AbstractDao<DFactory, String> {
  public DFactoryDao(Supplier supplier) {
    super(new DFactoryMapper(supplier), supplier);
  }
}
