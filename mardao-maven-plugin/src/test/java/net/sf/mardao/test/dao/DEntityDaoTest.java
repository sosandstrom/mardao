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
import net.sf.mardao.dao.TransactionHolder;
import net.sf.mardao.test.domain.DEntity;

/**
 * Tests the {@link net.sf.mardao.test.dao.GeneratedDEntityDaoImpl} DAO methods.
 *
 * @author osandstrom Date: 2014-09-14 Time: 18:47
 */
public class DEntityDaoTest {

  private DEntityDaoBean dao;

  @Before
  public void setUp() {
    Supplier supplier = new InMemorySupplier();
    dao = new DEntityDaoBean(supplier);
  }

  @Test
  public void testWriteReadUser() throws IOException {
    DEntity actual = dao.withCommitTransaction(new TransFunc<DEntity>() {
      @Override
      public DEntity apply() throws IOException {
        DEntity entity = new DEntity();
        entity.setId(327L);
        entity.setDisplayName("xHjqLåäö123");

        DEntity actual = dao.get(327L);
        assertNull(actual);

        Long id = dao.put(entity);
        assertEquals(entity.getId(), id);
        return  dao.get(id);
      }
    });
    assertNotNull(actual);
    assertEquals(Long.valueOf(327L), actual.getId());
    assertEquals("xHjqLåäö123", actual.getDisplayName());
  }

}
