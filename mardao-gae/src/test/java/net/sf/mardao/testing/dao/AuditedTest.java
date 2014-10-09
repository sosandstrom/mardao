package net.sf.mardao.testing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import net.sf.mardao.dao.AbstractDao;
import net.sf.mardao.dao.DatastoreSupplier;
import net.sf.mardao.testing.domain.DAudited;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 2014-10-09 Time: 19:25
 */
public class AuditedTest {
  public static final String PRINCIPAL_SET_UP = "setUp";
  public static final String PRINCIPAL_UPDATE = "Updater";

  final LocalServiceTestHelper helper = new LocalServiceTestHelper(
    new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(1));
  private DatastoreSupplier supplier;
  private DAuditedDaoBean dao;

  @Before
  public void setUp() {
    helper.setUp();
    supplier = new DatastoreSupplier();
    dao = new DAuditedDaoBean(supplier);
    AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
  }

  @Test
  public void testAuditInfo() throws IOException {
    DAudited entity = new DAudited();
    entity.setDisplayName("Stephen Holder");

    // create
    Long id = dao.put(entity);
    assertNotNull(id);
    assertEquals(id, entity.getId());
    assertEquals(PRINCIPAL_SET_UP, entity.getCreatedBy());
    assertNotNull(entity.getCreatedDate());
    assertEquals(PRINCIPAL_SET_UP, entity.getUpdatedBy());
    assertNotNull(entity.getUpdatedDate());
    assertEquals("Stephen Holder", entity.getDisplayName());

    // update
    Date createdDate = entity.getCreatedDate();
    AbstractDao.setPrincipalName(PRINCIPAL_UPDATE);
    Long actual = dao.put(entity);
    assertEquals(id, actual);
    assertEquals(id, entity.getId());
    assertEquals(PRINCIPAL_SET_UP, entity.getCreatedBy());
    assertEquals(createdDate, entity.getCreatedDate());
    assertEquals(PRINCIPAL_UPDATE, entity.getUpdatedBy());
    assertTrue(entity.getUpdatedDate().after(createdDate));
    assertEquals("Stephen Holder", entity.getDisplayName());
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}