package net.sf.mardao.dao;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * Tests for DatastoreSupplier.
 *
 * @author osandstrom Date: 2014-09-12 Time: 20:17
 */
public class DatastoreSupplierTest extends AbstractDaoTest {

  final LocalServiceTestHelper helper = new LocalServiceTestHelper(
    new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  @Before
  @Override
  public void setUp() {
    helper.setUp();
    Supplier supplier = new DatastoreSupplier();
    userDao = new DUserDao(supplier);
    factoryDao = new DFactoryDao(supplier);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}
