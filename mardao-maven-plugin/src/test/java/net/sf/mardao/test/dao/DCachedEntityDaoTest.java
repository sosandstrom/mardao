package net.sf.mardao.test.dao;

import net.sf.mardao.dao.InMemorySupplier;
import net.sf.mardao.dao.Supplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DCachedEntityDaoTest {

  private DCachedEntityDaoBean dao;

  @Before
  public void setUp() throws Exception {
    Supplier supplier = new InMemorySupplier();
    dao = new DCachedEntityDaoBean(supplier);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCrudAnnotations() throws Exception {
    // TODO
  }

  @Test
  public void testClassAnnotation() throws Exception {
    // TODO
  }

  @Test
  public void testPageAnnotation() throws Exception {
    // TODO
  }

}