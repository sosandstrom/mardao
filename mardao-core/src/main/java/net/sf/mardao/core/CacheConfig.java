package net.sf.mardao.core;

import javax.cache.annotation.CacheKeyGenerator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put this annotation on either method or class level together with jsr107 cache annotations to provide additional cache configuration options.
 * The annotation will be used the first time the cache is created. All operations for the same cache name will have the same configuration.
 * This annotation has higher priority then the CacheDefaults annotation.
 *
 * @author mattiaslevin
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheConfig {

  /**
   * Cache name.
   */
  String name() default "";

  /**
   * Size of the cache.
   */
  long size() default -1;

  /**
   * Cache expiration.
   */
  int expiresAfterSeconds() default -1;

  /**
   * The class that will be used to generate the cache key.
   */
  Class<? extends CacheKeyGenerator> keyGenerator() default CacheKeyGenerator.class;

}
