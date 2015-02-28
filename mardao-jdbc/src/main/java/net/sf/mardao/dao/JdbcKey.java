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
 * Key class for {@link JdbcSupplier}.
 *
 * @author osandstrom Date: 2014-09-21 Time: 18:09
 */
public class JdbcKey {
  private final JdbcKey parentKey;
  private final String kind;
  private final String name;
  private final Long id;

  public JdbcKey(JdbcKey parentKey, String kind, String name) {
    if (null == kind) {
      throw new IllegalArgumentException("Kind cannot be null");
    }
    this.parentKey = parentKey;
    this.kind = kind;
    this.name = name;
    this.id = null;
  }

  public JdbcKey(JdbcKey parentKey, String kind, Long id) {
    if (null == kind) {
      throw new IllegalArgumentException("Kind cannot be null");
    }
    this.parentKey = parentKey;
    this.kind = kind;
    this.name = null;
    this.id = id;
  }

  public static JdbcKey of(JdbcKey parentKey, String kind, String name) {
    return new JdbcKey(parentKey, kind, name);
  }
  public static JdbcKey of(JdbcKey parentKey, String kind, Long id) {
    return new JdbcKey(parentKey, kind, id);
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }
    if (!(obj instanceof JdbcKey)) {
      return false;
    }
    JdbcKey other = (JdbcKey) obj;
    if (null == this.parentKey) {
      if (null != other.parentKey) {
        return false;
      }
      return this.kind.equals(other.kind) && objectsEqual(this.name, other.name) && objectsEqual(this.id, other.id);
    }
    return this.parentKey.equals(other.parentKey) && this.kind.equals(other.kind) && objectsEqual(this.name, other.name) && objectsEqual(this.id, other.id);
  }

  @Override
  public int hashCode() {
    return (null != parentKey ? 31*31*parentKey.hashCode() : 0) + 31*kind.hashCode() + (null != name ? name.hashCode() : id.hashCode());
  }

  public String getKind() {
    return kind;
  }

  public String getName() {
    return name;
  }

  public Long getId() {
    return id;
  }

  public JdbcKey getParentKey() {
    return parentKey;
  }

  @Override
  public String toString() {
    return "Key{ parent:" + parentKey + ", kind:" + kind + ", name:" + name + ", id:" + id + "}";
  }

  public static boolean objectsEqual(Object one, Object other) {
    return null != one ? one.equals(other) : null == other;
  }
}
