package net.sf.mardao.dao;

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

/**
 * Key class for {@link net.sf.mardao.dao.InMemorySupplier}.
 *
 * @author osandstrom Date: 2014-09-21 Time: 18:09
 */
public class InMemoryKey {
  private final InMemoryKey parentKey;
  private final String kind;
  private final String name;

  public InMemoryKey(InMemoryKey parentKey, String kind, String name) {
    if (null == kind) {
      throw new IllegalArgumentException("Kind cannot be null");
    }
    this.parentKey = parentKey;
    this.kind = kind;
    this.name = name;
  }

  public static InMemoryKey of(InMemoryKey parentKey, String kind, String name) {
    return new InMemoryKey(parentKey, kind, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }
    if (!(obj instanceof InMemoryKey)) {
      return false;
    }
    InMemoryKey other = (InMemoryKey) obj;
    if (null == this.parentKey) {
      if (null != other.parentKey) {
        return false;
      }
      return this.kind.equals(other.kind) && this.name.equals(other.name);
    }
    return this.parentKey.equals(other.parentKey) && this.kind.equals(other.kind) && this.name.equals(other.name);
  }

  @Override
  public int hashCode() {
    return (null != parentKey ? 31*31*parentKey.hashCode() : 0) + 31*kind.hashCode() + name.hashCode();
  }

  public String getKind() {
    return kind;
  }

  public String getName() {
    return name;
  }

  public InMemoryKey getParentKey() {
    return parentKey;
  }

  @Override
  public String toString() {
    return "Key{ parent:" + parentKey + ", kind:" + kind + ", name:" + name + "}";
  }
}
