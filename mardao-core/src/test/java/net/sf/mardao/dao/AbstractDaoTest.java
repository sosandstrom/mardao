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

  @Before
  public void setUp() {
    Supplier supplier = new InMemorySupplier();
    userDao = new DUserDao(supplier);
    factoryDao = new DFactoryDao(supplier);
    AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
  }

  @Test
  public void testWriteReadUser() throws IOException {
    userDao.withCommitTransaction(new TransFunc<Void>() {
      @Override
      public Void apply(TransactionHolder tx) throws IOException {
        DUser entity = new DUser();
        entity.setId(327L);
        entity.setDisplayName("xHjqLåäö123");

        DUser actual = userDao.get(tx, 327L);
        assertNull(actual);

        Long id = userDao.put(tx, entity);
        assertEquals(entity.getId(), id);
        actual = userDao.get(tx, id);
        assertNotNull(actual);
        assertEquals(Long.valueOf(327L), actual.getId());
        assertEquals("xHjqLåäö123", actual.getDisplayName());
        return null;
      }
    });
  }

  @Test
  public void testWriteReadFactory() throws IOException {
    factoryDao.withRollbackTransaction(new TransFunc<Void>() {
      @Override
      public Void apply(TransactionHolder tx) throws IOException {
        DFactory entity = new DFactory();
        entity.setProviderId("mardao");

        String id = factoryDao.put(tx, entity);
        assertEquals(entity.getProviderId(), id);
        DFactory actual = factoryDao.get(tx, id);
        assertNotNull(actual);
        assertEquals("mardao", actual.getProviderId());
        return null;
      }
    });
  }

  @Test
  public void testQueryByField() throws IOException {
    userDao.withCommitTransaction(new TransFunc<Void>() {
      @Override
      public Void apply(TransactionHolder tx) throws IOException {
        createQueryFixtures(tx);

        Iterable<DUser> users = userDao.queryByDisplayName(tx, "mod7_2");
        int count = 0;
        for (DUser u : users) {
          count++;
          assertEquals("mod7_2", u.getDisplayName());
          assertEquals(2, u.getId() % 7);
        }
        assertEquals(9, count);

        users = userDao.queryByDisplayName(tx, null);
        assertFalse(users.iterator().hasNext());
        return null;
      }
    });
  }

  @Test
  public void testFindUniqueByField() throws IOException {
    DUser u47 = userDao.withCommitTransaction(new TransFunc<DUser>() {
      @Override
      public DUser apply(TransactionHolder tx) throws IOException {
        createQueryFixtures(tx);

        DUser u47n = userDao.findByEmail(tx, null);
        assertNull(u47n);

        return userDao.findByEmail(tx, "user_47@example.com");
      }
    });
    assertEquals(Long.valueOf(47), u47.getId());
    assertEquals("user_47@example.com", u47.getEmail());
  }

  @Test
  public void testCount() throws IOException {
    userDao.withRollbackTransaction(new TransFunc<Void>() {
      @Override
      public Void apply(TransactionHolder tx) throws IOException {
        createQueryFixtures(tx);
        assertEquals(118, userDao.count(tx));
        assertEquals(1, factoryDao.count(tx));
        return null;
      }
    });
  }

  @Test
  public void testDelete() throws IOException {
    userDao.withRollbackTransaction(new TransFunc<Void>() {
      @Override
      public Void apply(TransactionHolder tx) throws IOException {
        createQueryFixtures(tx);
        DUser actual = userDao.get(tx, 42L);
        assertNotNull(actual);

        userDao.delete(tx, 42L);
        actual = userDao.get(tx, 42L);
        assertNull(actual);
        assertEquals(117, userDao.count(tx));
        return null;
      }
    });
  }

  @Test
  public void testCreated() throws IOException {
    DUser actual = userDao.withCommitTransaction(new TransFunc<DUser>() {
      @Override
      public DUser apply(TransactionHolder tx) throws IOException {
        createQueryFixtures(tx);
        return userDao.get(tx, 42L);
      }
    });
    assertEquals(PRINCIPAL_FIXTURE, actual.getCreatedBy());
    assertNotNull(actual.getBirthDate());
  }

  public void testAuditInfoCreated() {
    DUser actual = new DUser();
    Date date = new Date();
    userDao.updateAuditInfo(actual, "first", date,
      "createdBy", "birthDate", null, null);
    assertEquals("first", actual.getCreatedBy());
    assertEquals(date, actual.getBirthDate());

    userDao.updateAuditInfo(actual, "second", new Date(0L),
      "createdBy", "birthDate", null, null);
    assertEquals("first", actual.getCreatedBy());
    assertEquals(date, actual.getBirthDate());
  }

  public void testAuditInfoUpdated() {
    DUser actual = new DUser();
    Date date = new Date();
    userDao.updateAuditInfo(actual, "first", date,
      null, null, "createdBy", "birthDate");
    assertEquals("first", actual.getCreatedBy());
    assertEquals(date, actual.getBirthDate());

    Date date1 = new Date(0L);
    userDao.updateAuditInfo(actual, "second", date1,
      null, null, "createdBy", "birthDate");
    assertEquals("second", actual.getCreatedBy());
    assertEquals(date1, actual.getBirthDate());

    // do not mess up
    userDao.updateAuditInfo(actual, null, null,
      null, null, "createdBy", "birthDate");
    assertEquals("second", actual.getCreatedBy());
    assertEquals(date1, actual.getBirthDate());
  }

  private void createQueryFixtures(TransactionHolder tx) throws IOException {
    AbstractDao.setPrincipalName(PRINCIPAL_FIXTURE);
    for (int i = 1; i < 60; i++) {
      DUser u = new DUser();
      u.setId(Long.valueOf(i));
      u.setDisplayName("mod7_" + (i % 7));
      u.setEmail("user_" + i + "@example.com");
      userDao.put(tx, u);

      u = new DUser();
      u.setId(Long.valueOf(1000 + i));
      u.setDisplayName("user_" + i);
      u.setEmail("user_1000_" + i + "@example.com");
      userDao.put(tx, u);
    }

    DFactory f = new DFactory();
    f.setProviderId("facebook");
    factoryDao.put(tx, f);
    AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
  }
}
