package net.sf.mardao.dao;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.mardao.domain.DFactory;
import net.sf.mardao.domain.DUser;

/**
 * Tests for AbstractDao.
 *
 * @author osandstrom Date: 2014-09-12 Time: 20:17
 */
public class AbstractDaoTest {

  protected DUserDao userDao;
  protected DFactoryDao factoryDao;

  @Before
  public void setUp() {
    Supplier supplier = new InMemorySupplier();
    userDao = new DUserDao(supplier);
    factoryDao = new DFactoryDao(supplier);
  }

  @Test
  public void testWriteReadUser() throws IOException {
    DUser entity = new DUser();
    entity.setId(327L);
    entity.setDisplayName("xHjqLåäö123");

    DUser actual = userDao.get(327L);
    assertNull(actual);

    Long id = userDao.put(entity);
    assertEquals(entity.getId(), id);
    actual = userDao.get(id);
    assertNotNull(actual);
    assertEquals(Long.valueOf(327L), actual.getId());
    assertEquals("xHjqLåäö123", actual.getDisplayName());
  }

  @Test
  public void testWriteReadFactory() throws IOException {
    DFactory entity = new DFactory();
    entity.setProviderId("mardao");

    String id = factoryDao.put(entity);
    assertEquals(entity.getProviderId(), id);
    DFactory actual = factoryDao.get(id);
    assertNotNull(actual);
    assertEquals("mardao", actual.getProviderId());
  }
}
