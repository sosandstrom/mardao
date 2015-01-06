package net.sf.mardao.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a class or method as possible to cache.
 * Created by sosandstrom on 2015-01-02.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Cached {
  String from() default "";
  long size() default -1;
  int expiresAfterSeconds() default -1;
  boolean cachePages() default false;
}
