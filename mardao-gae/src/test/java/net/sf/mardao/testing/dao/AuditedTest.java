package net.sf.mardao.testing.dao;

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