package net.sf.mardao.dao;

/*
 * #%L
 * mardao-gae
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
