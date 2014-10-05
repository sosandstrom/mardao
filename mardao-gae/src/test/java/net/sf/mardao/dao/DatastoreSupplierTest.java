package net.sf.mardao.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import net.sf.mardao.domain.DUser;
/**
 * Tests for DatastoreSupplier.
 *
 * @author osandstrom Date: 2014-09-12 Time: 20:17
 */
public class DatastoreSupplierTest extends AbstractDaoTest {

  final LocalServiceTestHelper helper = new LocalServiceTestHelper(
    new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(1));

  @Before
  @Override
  public void setUp() {
    helper.setUp();
    supplier = new DatastoreSupplier();
    userDao = new DUserDao(supplier);
    factoryDao = new DFactoryDao(supplier);
  }

  @Override
  @Test
  public void testCount() throws IOException {
    createQueryFixtures();
    try {
      Thread.sleep(150L);
    } catch (InterruptedException e) {
      throw new IOException("sleeping", e);
    }
    int count = userDao.count();
    assertTrue(Integer.toString(count), 114 <= count);
    assertEquals(1, factoryDao.count());
  }

  @Override
  @Test
  public void testQueryByField() throws IOException {
    createQueryFixtures();

    Iterable<DUser> users = userDao.queryByDisplayName("mod7_2");
    int count = 0;
    for (DUser u : users) {
      count++;
      assertEquals("mod7_2", u.getDisplayName());
      assertEquals(2, u.getId() % 7);
    }
    assertTrue(Integer.toString(count), 8 <= count);

    users = userDao.queryByDisplayName(null);
    assertFalse(users.iterator().hasNext());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
