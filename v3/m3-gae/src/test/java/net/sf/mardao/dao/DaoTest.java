package net.sf.mardao.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import net.sf.mardao.dao.InMemorySupplier;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.domain.DFactory;
import net.sf.mardao.domain.DUser;

/**
 * Tests for AbstractDao.
 *
 * @author osandstrom Date: 2014-09-12 Time: 20:17
 */
public class DaoTest {

  final LocalServiceTestHelper helper = new LocalServiceTestHelper(
    new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private DUserDao userDao;
  private DFactoryDao factoryDao;

  @Before
  public void setUp() {
    helper.setUp();
    Supplier supplier = new DatastoreSupplier();
    userDao = new DUserDao(supplier);
    factoryDao = new DFactoryDao(supplier);
  }

  @Test
  public void testWriteReadUser() throws IOException {
    DUser entity = new DUser();
    entity.setId(327L);
    entity.setDisplayName("xHjqLåäö123");

    Long id = userDao.put(entity);
    assertEquals(entity.getId(), id);
    DUser actual = userDao.get(id);
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

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
