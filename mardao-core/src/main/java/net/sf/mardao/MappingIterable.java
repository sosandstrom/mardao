package net.sf.mardao;

/*
 * #%L
 * mardao-core
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
