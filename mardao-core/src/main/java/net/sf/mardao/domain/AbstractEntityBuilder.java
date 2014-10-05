package net.sf.mardao.domain;

/**
 * Builder for Dao Entities.
 *
 * @author osandstrom Date: 2014-10-05 Time: 08:27
 */
public abstract class AbstractEntityBuilder<T> {

  protected final T entity;

  protected AbstractEntityBuilder() {
    entity = newInstance();
  }

  protected abstract T newInstance();

  public T build() {
    return entity;
  }
}
