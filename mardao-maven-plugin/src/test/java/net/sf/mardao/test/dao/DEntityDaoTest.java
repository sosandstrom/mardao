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
import net.sf.mardao.test.domain.DEntity;

/**
 * Tests the {@link net.sf.mardao.test.dao.GeneratedDEntityDaoImpl} DAO methods.
 *
 * @author osandstrom Date: 2014-09-14 Time: 18:47
 */
public class DEntityDaoTest {

  private DEntityDaoBean dao;
  private Supplier supplier;

  @Before
  public void setUp() {
    supplier = new InMemorySupplier();
    dao = new DEntityDaoBean(supplier);
  }

  @Test
  public void testWriteReadUser() throws IOException {
    DEntity actual = dao.withCommitTransaction(new TransFunc<DEntity>() {
      @Override
      public DEntity apply() throws IOException {
        DEntity entity = new DEntity();
        entity.setId(327L);
        entity.setDisplayName("xHjqLåäö123");

        DEntity actual = dao.get(327L);
        assertNull(actual);

        Long id = dao.put(entity);
        assertEquals(entity.getId(), id);
        return  dao.get(id);
      }
    });
    assertNotNull(actual);
    assertEquals(Long.valueOf(327L), actual.getId());
    assertEquals("xHjqLåäö123", actual.getDisplayName());
  }

  @Test
  public void testKind() {
    DEntityMapper mapper = new DEntityMapper(supplier);
    assertEquals(DEntity.class.getSimpleName(), mapper.getKind());
  }
}
