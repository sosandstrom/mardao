package net.sf.mardao.test.dao;

/*
 * #%L
 * net.sf.mardao:mardao-maven-plugin
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
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.mardao.dao.InMemorySupplier;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.dao.TransFunc;
import net.sf.mardao.test.domain.DChild;
import net.sf.mardao.test.domain.DEntity;

/**
 * Tests the {@link GeneratedDEntityDaoImpl} DAO methods.
 *
 * @author osandstrom Date: 2014-09-14 Time: 18:47
 */
public class DChildDaoTest {

  private DEntityDaoBean parentDao;
  private DChildDaoBean childDao;

  @Before
  public void setUp() {
    Supplier supplier = new InMemorySupplier();
    parentDao = new DEntityDaoBean(supplier);
    childDao = new DChildDaoBean(supplier);
  }

  @Test
  public void testWriteReadChild() throws IOException {
    DEntity entity = parentDao.withCommitTransaction(new TransFunc<DEntity>() {
      @Override
      public DEntity apply() throws IOException {
        DEntity entity = new DEntity();
        entity.setId(327L);
        entity.setDisplayName("xHjqLåäö123");

        DEntity actual = parentDao.get(327L);
        assertNull(actual);

        Long id = parentDao.put(entity);
        assertEquals(entity.getId(), id);
        return  parentDao.get(id);
      }
    });
    assertNotNull(entity);
    assertEquals(Long.valueOf(327L), entity.getId());
    assertEquals("xHjqLåäö123", entity.getDisplayName());

    DChild child = new DChild();
    final Object entityKey = parentDao.getKey(entity.getId());
    child.setParentEntityKey(entityKey);
    child.setAccessToken("abc123");
    String simpleKey = childDao.put(child);

    assertEquals("abc123", simpleKey);

    DChild actual = childDao.get(entityKey, simpleKey);
    assertEquals(entityKey, actual.getParentEntityKey());
  }

}
