package net.sf.mardao.test.dao;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.sf.mardao.core.CacheConfig;
import net.sf.mardao.test.domain.DCachedEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.cache.annotation.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DCachedEntityDaoTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCrudAnnotations() throws Exception {

    // Check that jsr107 annotations were added to crud methods

    Method readMethod = DCachedEntityDaoBean.class.getMethod("get", Object.class, Long.class);
    assertTrue(readMethod.isAnnotationPresent(CacheResult.class));
    Annotation[][] paramsAnnotations = readMethod.getParameterAnnotations();
    assertTrue(hasAnnotation(Arrays.asList(paramsAnnotations[0]), CacheKey.class));
    assertTrue(hasAnnotation(Arrays.asList(paramsAnnotations[1]), CacheKey.class));

    Method putMethod = DCachedEntityDaoBean.class.getMethod("put", Object.class, Long.class, DCachedEntity.class);
    assertTrue(putMethod.isAnnotationPresent(CachePut.class));
    paramsAnnotations = putMethod.getParameterAnnotations();
    assertTrue(hasAnnotation(Arrays.asList(paramsAnnotations[0]), CacheKey.class));
    assertTrue(hasAnnotation(Arrays.asList(paramsAnnotations[1]), CacheKey.class));
    assertTrue(hasAnnotation(Arrays.asList(paramsAnnotations[2]), CacheValue.class));

    Method removeMethod = DCachedEntityDaoBean.class.getMethod("delete", Object.class, Long.class);
    assertTrue(removeMethod.isAnnotationPresent(CacheRemove.class));
    paramsAnnotations = removeMethod.getParameterAnnotations();
    assertTrue(hasAnnotation(Arrays.asList(paramsAnnotations[0]), CacheKey.class));
    assertTrue(hasAnnotation(Arrays.asList(paramsAnnotations[1]), CacheKey.class));

  }


  private static boolean hasAnnotation(Collection<Annotation> annotations, Class<? extends Annotation> annotationClass) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().equals(annotationClass)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void testClassAnnotation() throws Exception {
    assertTrue(DCachedEntityDaoBean.class.isAnnotationPresent(CacheConfig.class));
  }

  @Test
  public void testPageAnnotation() throws Exception {

    // The read page method should not be annotated
    Method readMethod = DCachedEntityDaoBean.class.getMethod("queryPage", Object.class, Integer.TYPE, String.class);
    assertFalse(readMethod.isAnnotationPresent(CacheResult.class));

  }

}