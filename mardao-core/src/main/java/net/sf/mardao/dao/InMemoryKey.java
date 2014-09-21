package net.sf.mardao.dao;

/**
 * Key class for {@link net.sf.mardao.dao.InMemorySupplier}.
 *
 * @author osandstrom Date: 2014-09-21 Time: 18:09
 */
public class InMemoryKey {
  private final String kind;
  private final String name;

  public InMemoryKey(String kind, String name) {
    if (null == kind || null == name) {
      throw new IllegalArgumentException("Cannot be null " + kind + " or " + name);
    }
    this.kind = kind;
    this.name = name;
  }

  public static InMemoryKey of(String kind, String name) {
    return new InMemoryKey(kind, name);
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
    return this.kind.equals(other.kind) && this.name.equals(other.name);
  }

  @Override
  public int hashCode() {
    return 31*kind.hashCode() + name.hashCode();
  }

  public String getKind() {
    return kind;
  }

  public String getName() {
    return name;
  }
}
