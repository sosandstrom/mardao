package net.sf.mardao.test.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.cache.annotation.CacheResult;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class DPageCachedEntityTest {


  @Test
  public void testPageAnnotation() throws Exception {

    // The read page method should not be annotated
    Method readMethod = DPageCachedEntityDaoBean.class.getMethod("queryPage", Object.class, Integer.TYPE, String.class);
    assertTrue(readMethod.isAnnotationPresent(CacheResult.class));

  }

}