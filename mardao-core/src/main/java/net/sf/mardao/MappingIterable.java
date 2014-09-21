package net.sf.mardao;

import java.io.Serializable;
import java.util.Iterator;

import net.sf.mardao.dao.Mapper;

/**
 * An Iterator that uses the Mapper on the fly.
 *
 * @author osandstrom Date: 2014-09-15 Time: 19:33
 */
public class MappingIterable<T, ID extends Serializable> implements Iterable<T>, Iterator<T> {

  private final Mapper<T, ID> mapper;
  private final Iterator iterator;

  public MappingIterable(Mapper<T, ID> mapper, Iterator iterator) {
    this.mapper = mapper;
    this.iterator = iterator;
  }

  /**
   * Returns an iterator over a set of elements of type T.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator<T> iterator() {
    return this;
  }

  /**
   * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true} if {@link #next} would return
   * an element rather than throwing an exception.)
   *
   * @return {@code true} if the iteration has more elements
   */
  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration
   */
  @Override
  public T next() {
    final Object value = iterator.next();
    final T entity = mapper.fromReadValue(value);
    return entity;
  }

  /**
   * Removes from the underlying collection the last element returned by this iterator (optional operation).  This method can be
   * called only once per call to {@link #next}.  The behavior of an iterator is unspecified if the underlying collection is
   * modified while the iteration is in progress in any way other than by calling this method.
   *
   * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this iterator
   * @throws IllegalStateException         if the {@code next} method has not yet been called, or the {@code remove} method has
   *                                       already been called after the last call to the {@code next} method
   */
  @Override
  public void remove() {
    iterator.remove();
  }
}
