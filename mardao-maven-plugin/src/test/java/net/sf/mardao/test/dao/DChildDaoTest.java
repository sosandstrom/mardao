package net.sf.mardao.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.mardao.dao.InMemorySupplier;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.dao.TransFunc;
import net.sf.mardao.test.domain.DChild;
import net.sf.mardao.test.domain.DEntity;

/**
 * Tests the {@link GeneratedDEntityDaoImpl} DAO methods.
 *
 * @author osandstrom Date: 2014-09-14 Time: 18:47
 */
public class DChildDaoTest {

  private DEntityDaoBean parentDao;
  private DChildDaoBean childDao;

  @Before
  public void setUp() {
    Supplier supplier = new InMemorySupplier();
    parentDao = new DEntityDaoBean(supplier);
    childDao = new DChildDaoBean(supplier);
  }

  @Test
  public void testWriteReadChild() throws IOException {
    DEntity entity = parentDao.withCommitTransaction(new TransFunc<DEntity>() {
      @Override
      public DEntity apply() throws IOException {
        DEntity entity = new DEntity();
        entity.setId(327L);
        entity.setDisplayName("xHjqLåäö123");

        DEntity actual = parentDao.get(327L);
        assertNull(actual);

        Long id = parentDao.put(entity);
        assertEquals(entity.getId(), id);
        return  parentDao.get(id);
      }
    });
    assertNotNull(entity);
    assertEquals(Long.valueOf(327L), entity.getId());
    assertEquals("xHjqLåäö123", entity.getDisplayName());

    DChild child = new DChild();
    final Object entityKey = parentDao.getKey(entity.getId());
    child.setParentEntityKey(entityKey);
    child.setAccessToken("abc123");
    String simpleKey = childDao.put(child);

    assertEquals("abc123", simpleKey);

    DChild actual = childDao.get(entityKey, simpleKey);
    assertEquals(entityKey, actual.getParentEntityKey());
  }

}
