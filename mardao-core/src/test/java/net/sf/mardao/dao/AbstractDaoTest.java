package net.sf.mardao.dao;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

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

  public static final String PRINCIPAL_FIXTURE = "fixture";
  public static final String PRINCIPAL_SET_UP = "setUp";
  protected DUserDao userDao;
  protected DFactoryDao factoryDao;
  protected Supplier supplier;

  @Before
  public void setUp() {
    supplier = new InMemorySupplier();
    userDao = new DUserDao(supplier);
    factoryDao = new DFactoryDao(supplier);
    AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
  }

  @Test
  public void testWriteReadUser() throws IOException {
    Long id = userDao.withCommitTransaction(new TransFunc<Long>() {
      @Override
      public Long apply() throws IOException {
        DUser entity = new DUser();
        entity.setId(327L);
        entity.setDisplayName("xHjqLåäö123");

        DUser actual = userDao.get(327L);
        assertNull(actual);

        return userDao.put(entity);
      }
    });
    assertEquals(Long.valueOf(327L), id);
    DUser actual = userDao.withCommitTransaction(new TransFunc<DUser>() {
      @Override
      public DUser apply() throws IOException {
        return userDao.get(327L);
      }
    });
    assertNotNull(actual);
    assertEquals(Long.valueOf(327L), actual.getId());
    assertEquals("xHjqLåäö123", actual.getDisplayName());
  }

  @Test
  public void testWriteReadFactory() throws IOException {
    final String name = factoryDao.withCommitTransaction(new TransFunc<String>() {
      @Override
      public String apply() throws IOException {
        DFactory entity = new DFactory();
        entity.setProviderId("mardao");

        String id = factoryDao.put(entity);
        return id;
      }
    });
    assertEquals("mardao", name);
    factoryDao.withRollbackTransaction(new TransFunc<Void>() {
      @Override
      public Void apply() throws IOException {
        DFactory actual = factoryDao.get(name);
        assertNotNull(actual);
        assertEquals("mardao", actual.getProviderId());
        return null;
      }
    });
  }

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
      assertEquals(9, count);

      users = userDao.queryByDisplayName(null);
      assertFalse(users.iterator().hasNext());
  }

  @Test
  public void testFindUniqueByField() throws IOException {
    createQueryFixtures();

    DUser u47 = userDao.findByEmail(null);
    assertNull(u47);

    u47 = userDao.findByEmail("user_47@example.com");
    assertEquals(Long.valueOf(47), u47.getId());
    assertEquals("user_47@example.com", u47.getEmail());
  }

  @Test
  public void testCount() throws IOException {
      createQueryFixtures();
      assertEquals(118, userDao.count());
      assertEquals(1, factoryDao.count());
  }

  @Test
  public void testDelete() throws IOException {
      createQueryFixtures();
      DUser actual = userDao.get(42L);
      assertNotNull(actual);

      userDao.delete(42L);
      actual = userDao.get(42L);
      assertNull(actual);
      assertEquals(117, userDao.count());
  }

  @Test
  public void testCreated() throws IOException {
    createQueryFixtures();
    DUser actual =  userDao.get(42L);

    assertEquals(PRINCIPAL_FIXTURE, actual.getCreatedBy());
    assertNotNull(actual.getBirthDate());
  }

  @Test
  public void testAuditInfoCreated() {
    Object key = supplier.toKey("DUser", 1L);
    Object actual = supplier.createWriteValue(key);
    Date date = new Date();
    userDao.updateAuditInfo(actual, "first", date,
      "createdBy", "birthDate", null, null);
    assertEquals("first", supplier.getString(actual, "createdBy"));
    assertEquals(date, supplier.getDate(actual, "birthDate"));

    userDao.updateAuditInfo(actual, "second", new Date(0L),
      "createdBy", "birthDate", null, null);
    assertEquals("first", supplier.getString(actual, "createdBy"));
    assertEquals(date, supplier.getDate(actual, "birthDate"));
  }

  @Test
  public void testAuditInfoUpdated() {
    Object key = supplier.toKey("DUser", 1L);
    Object actual = supplier.createWriteValue(key);
    Date date = new Date();
    userDao.updateAuditInfo(actual, "first", date,
      null, null, "createdBy", "birthDate");
    assertEquals("first", supplier.getString(actual, "createdBy"));
    assertEquals(date, supplier.getDate(actual, "birthDate"));

    Date date1 = new Date(0L);
    userDao.updateAuditInfo(actual, "second", date1,
      null, null, "createdBy", "birthDate");
    assertEquals("second", supplier.getString(actual, "createdBy"));
    assertEquals(date1, supplier.getDate(actual, "birthDate"));

    // do not mess up
    userDao.updateAuditInfo(actual, null, null,
      null, null, "createdBy", "birthDate");
    assertEquals("second", supplier.getString(actual, "createdBy"));
    assertEquals(date1, supplier.getDate(actual, "birthDate"));
  }

  protected void createQueryFixtures() throws IOException {
    AbstractDao.setPrincipalName(PRINCIPAL_FIXTURE);
    for (int i = 1; i < 60; i++) {
      DUser u = new DUser();
      u.setId(Long.valueOf(i));
      u.setDisplayName("mod7_" + (i % 7));
      u.setEmail("user_" + i + "@example.com");
      userDao.put(u);

      u = new DUser();
      u.setId(Long.valueOf(1000 + i));
      u.setDisplayName("user_" + i);
      u.setEmail("user_1000_" + i + "@example.com");
      userDao.put(u);
    }

    DFactory f = new DFactory();
    f.setProviderId("facebook");
    factoryDao.put(f);
    AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
  }
}
