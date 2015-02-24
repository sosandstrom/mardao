package net.sf.mardao.test.junit;

/*
 * #%L
 * mardao-core
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import net.sf.mardao.dao.AbstractDao;
import net.sf.mardao.dao.InMemorySupplier;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.test.dao.DBasicDaoBean;
import net.sf.mardao.test.domain.DBasic;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for AbstractDao.
 *
 * @author osandstrom Date: 2014-09-12 Time: 20:17
 */
public class DBasicDaoTest {

  public static final String PRINCIPAL_FIXTURE = "fixture";
  public static final String PRINCIPAL_SET_UP = "setUp";
  protected DBasicDaoBean basicDao;
  protected Supplier supplier;

  /** Override to test specific supplier */
  protected Supplier createSupplier() {
    return new InMemorySupplier();
  }

  @Before
  public void setUp() {
    supplier = createSupplier();
    basicDao = new DBasicDaoBean(supplier);
    AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
  }


  protected void createQueryFixtures() throws IOException {

    AbstractDao.setPrincipalName(PRINCIPAL_FIXTURE);

    for (int i = 1; i < 60; i++) {
      DBasic u = DBasicDaoBean.newBuilder()
              .id(Long.valueOf(i))
              .displayName("mod7_" + (i % 7))
              .build();
      basicDao.insert(null, Long.valueOf(i), u);

      u = DBasicDaoBean.newBuilder()
              .id(Long.valueOf(1000 + i))
              .displayName("user_" + i)
              .build();
      basicDao.insert(null, u.getId(), u);
    }

    AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
  }

  @Test
  public void testCreateGenerateId() throws IOException {
    DBasic actual = DBasicDaoBean.newBuilder()
            .displayName("Hello There")
            .build();
    basicDao.insert(null, null, actual);

    assertNotNull(actual.getId());
    assertEquals(PRINCIPAL_SET_UP, actual.getCreatedBy());
    assertNotNull(actual.getCreatedDate());
    assertNotNull(actual.getUpdatedDate());
  }

//  @Test
//  public void testWriteReadBasic() throws IOException {
//    Long id = userDao.withCommitTransaction(new TransFunc<Long>() {
//      @Override
//      public Long apply() throws IOException {
//        DUser entity = new DUser();
//        entity.setId(327L);
//        entity.setDisplayName("xHjqLåäö123");
//
//        DUser actual = userDao.get(327L);
//        assertNull(actual);
//
//        return userDao.put(entity);
//      }
//    });
//    assertEquals(Long.valueOf(327L), id);
//    DUser actual = userDao.withCommitTransaction(new TransFunc<DUser>() {
//      @Override
//      public DUser apply() throws IOException {
//        return userDao.get(327L);
//      }
//    });
//    assertNotNull(actual);
//    assertEquals(Long.valueOf(327L), actual.getId());
//    assertEquals("xHjqLåäö123", actual.getDisplayName());
//  }
//
//  @Test
//  public void testWriteReadFuture() throws IOException, ExecutionException, InterruptedException {
//    DUser entity = new DUser();
//    entity.setId(327L);
//    entity.setDisplayName("xHjqLåäö123");
//
//    DUser actual = userDao.get(327L);
//    assertNull(actual);
//
//    Future<Long> future = userDao.putAsync(entity);
//    Long id = future.get();
//    assertEquals(Long.valueOf(327L), id);
//
//    Future<DUser> read = userDao.getAsync(null, 327L);
//    actual = read.get();
//    assertNotNull(actual);
//    assertEquals(Long.valueOf(327L), actual.getId());
//    assertEquals("xHjqLåäö123", actual.getDisplayName());
//  }
//
//  @Test
//  public void testWriteReadFactory() throws IOException {
//    final String name = factoryDao.withCommitTransaction(new TransFunc<String>() {
//      @Override
//      public String apply() throws IOException {
//        DFactory entity = new DFactory();
//        entity.setProviderId("mardao");
//
//        String id = factoryDao.put(entity);
//        return id;
//      }
//    });
//    assertEquals("mardao", name);
//    factoryDao.withRollbackTransaction(new TransFunc<Void>() {
//      @Override
//      public Void apply() throws IOException {
//        DFactory actual = factoryDao.get(name);
//        assertNotNull(actual);
//        assertEquals("mardao", actual.getProviderId());
//        return null;
//      }
//    });
//  }
//
//  @Test
//  public void testQueryByField() throws IOException {
//      createQueryFixtures();
//
//      Iterable<DUser> users = userDao.queryByDisplayName("mod7_2");
//      int count = 0;
//      for (DUser u : users) {
//        count++;
//        assertEquals("mod7_2", u.getDisplayName());
//        assertEquals(2, u.getId() % 7);
//      }
//      assertEquals(9, count);
//
//      users = userDao.queryByDisplayName(null);
//      assertFalse(users.iterator().hasNext());
//  }
//
//  @Test
//  public void testFindUniqueByField() throws IOException {
//    createQueryFixtures();
//
//    DUser u47 = userDao.findByEmail(null);
//    assertNull(u47);
//
//    u47 = userDao.findByEmail("user_47@example.com");
//    assertEquals(Long.valueOf(47), u47.getId());
//    assertEquals("user_47@example.com", u47.getEmail());
//  }
//
//  @Test
//  public void testCount() throws IOException {
//      createQueryFixtures();
//      assertEquals(118, userDao.count());
//      assertEquals(1, factoryDao.count());
//  }
//
//  @Test
//  public void testDelete() throws IOException {
//      createQueryFixtures();
//      DUser actual = userDao.get(42L);
//      assertNotNull(actual);
//
//      userDao.delete(42L);
//      actual = userDao.get(42L);
//      assertNull(actual);
//      assertEquals(117, userDao.count());
//  }
//
//  @Test
//  public void testCreated() throws IOException {
//    createQueryFixtures();
//    DUser actual =  userDao.get(42L);
//
//    assertEquals(PRINCIPAL_FIXTURE, actual.getCreatedBy());
//    assertNotNull(actual.getBirthDate());
//  }
//
//  @Test
//  public void testAuditInfoCreated() {
//    DUserMapper mapper = new DUserMapper(supplier);
//    Object actual = supplier.createWriteValue(mapper, null, 1L, null);
//    Date date = new Date();
//    userDao.updateAuditInfo(actual, "first", date,
//      "createdBy", "birthDate", null, null);
//    assertEquals("first", supplier.getString(actual, "createdBy"));
//    assertEquals(date, supplier.getDate(actual, "birthDate"));
//
//    userDao.updateAuditInfo(actual, "second", new Date(0L),
//      "createdBy", "birthDate", null, null);
//    assertEquals("first", supplier.getString(actual, "createdBy"));
//    assertEquals(date, supplier.getDate(actual, "birthDate"));
//  }
//
//  @Test
//  public void testAuditInfoUpdated() {
//    DUserMapper mapper = new DUserMapper(supplier);
//    Object actual = supplier.createWriteValue(mapper, null, 1L, null);
//    Date date = new Date();
//    userDao.updateAuditInfo(actual, "first", date,
//            null, null, "createdBy", "birthDate");
//    assertEquals("first", supplier.getString(actual, "createdBy"));
//    assertEquals(date, supplier.getDate(actual, "birthDate"));
//
//    Date date1 = new Date(0L);
//    userDao.updateAuditInfo(actual, "second", date1,
//      null, null, "createdBy", "birthDate");
//    assertEquals("second", supplier.getString(actual, "createdBy"));
//    assertEquals(date1, supplier.getDate(actual, "birthDate"));
//
//    // do not mess up
//    userDao.updateAuditInfo(actual, null, null,
//            null, null, "createdBy", "birthDate");
//    assertEquals("second", supplier.getString(actual, "createdBy"));
//    assertEquals(date1, supplier.getDate(actual, "birthDate"));
//  }
//
//  @Test
//  public void testQueryPage() throws IOException {
//    createQueryFixtures();
//    Filter filter = Filter.equalsFilter(DUserMapper.Field.DISPLAYNAME.getFieldName(), "mod7_3");
//    CursorPage<DUser> firstPage = userDao.queryPage(false, 5, null,
//      null, false, null, false,
//      null, null,
//      filter);
//    assertEquals(Integer.valueOf(9), firstPage.getTotalSize());
//    assertNotNull(firstPage.getCursorKey());
//    assertEquals(5, firstPage.getItems().size());
//
//    CursorPage<DUser> secondPage = userDao.queryPage(false, 5, null,
//      null, false, null, false,
//      null, firstPage.getCursorKey(), filter);
//    assertNull(secondPage.getTotalSize());
//    assertNull(secondPage.getCursorKey());
//    assertEquals(4, secondPage.getItems().size());
//  }
}
