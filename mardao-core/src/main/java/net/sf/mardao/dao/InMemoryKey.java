package net.sf.mardao.dao;

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
