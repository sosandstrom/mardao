package net.sf.mardao.junit;

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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import net.sf.mardao.dao.DatastoreSupplier;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.test.junit.DBasicDaoTest;
import org.junit.After;
import org.junit.Before;

/**
 * To test DatastoreSupplier.
 *
 * @author osandstrom Date: 2015-02-26 Time: 19:25
 */
public class DatastoreBasicTest extends DBasicDaoTest {

  final LocalServiceTestHelper helper = new LocalServiceTestHelper(
    new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(1));

  @Override
  protected Supplier createSupplier() {
    return new DatastoreSupplier();
  }

  @Before
  public void setUp() {
    helper.setUp();
    super.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }
}