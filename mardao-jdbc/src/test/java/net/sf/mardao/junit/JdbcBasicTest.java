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

import net.sf.mardao.dao.JdbcSupplier;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.test.junit.DBasicDaoTest;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

/**
 * To test basic JdbcSupplier features.
 *
 * @author osandstrom Date: 2015-02-24 Time: 19:25
 */
public class JdbcBasicTest extends DBasicDaoTest {

  final DataSource dataSource = new SingleConnectionDataSource(
          "jdbc:h2:mem:typeDaoTest", "mardao", "jUnit", true);
  final InMemoryDataFieldMaxValueIncrementer incrementer =
          new InMemoryDataFieldMaxValueIncrementer();

  @Override
  protected Supplier createSupplier() {
    return new JdbcSupplier(dataSource, incrementer);
  }

  @Before
  @Override
  public void setUp() {
    createDatabaseTables(dataSource);
    super.setUp();
  }

  public static void createDatabaseTables(DataSource dataSource) {
    final JdbcTemplate template = new JdbcTemplate(dataSource);
    String sql = "CREATE TABLE DBasic (id BIGINT PRIMARY KEY, displayName VARCHAR(500), createdBy VARCHAR(500), createdDate TIMESTAMP, updatedBy VARCHAR(500), updatedDate TIMESTAMP)";
    template.execute(sql);
  }

  @After
  public void tearDown() {
    dropDatabaseTables(dataSource);
  }

  public static void dropDatabaseTables(DataSource dataSource) {
    final JdbcTemplate template = new JdbcTemplate(dataSource);

    String sql = "DROP TABLE DBasic";
    template.execute(sql);
  }
}