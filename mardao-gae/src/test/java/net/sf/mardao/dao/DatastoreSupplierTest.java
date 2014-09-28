package net.sf.mardao.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

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
    userDao.withoutTransaction(new TransFunc<Void>() {
      @Override
      public Void apply(TransactionHolder tx) throws IOException {
        createQueryFixtures(tx);
        try {
          Thread.sleep(150L);
        } catch (InterruptedException e) {
          throw new IOException("sleeping", e);
        }
        int count = userDao.count(tx);
        assertTrue(Integer.toString(count), 116 <= count);
        assertEquals(1, factoryDao.count(tx));
        return null;
      }
    });
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
