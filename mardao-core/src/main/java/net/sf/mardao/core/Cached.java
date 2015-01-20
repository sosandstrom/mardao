package net.sf.mardao.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation the domain object to enable caching of CRUD operations.
 *
 * @author mattiaslevin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

  /**
   * Cache query page.
   */
  boolean cachePages() default false;

}
