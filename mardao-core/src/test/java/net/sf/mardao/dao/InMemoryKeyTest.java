package net.sf.mardao.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;

import org.junit.Test;

/**
 * @author osandstrom Date: 2014-09-23 Time: 20:02
 */
public class InMemoryKeyTest {

  @Test
  public void testInMemoryKey() {
    HashSet<InMemoryKey> set = new HashSet<InMemoryKey>();

    InMemoryKey one = new InMemoryKey(null, "one", "one");
    set.add(one);

    InMemoryKey uno = new InMemoryKey(null, "one", "one");
    set.add(uno);
    assertEquals(one, uno);
    assertEquals(1, set.size());

    InMemoryKey two = new InMemoryKey(null, "one", "two");
    set.add(two);
    assertNotEquals(one, two);
    assertEquals(2, set.size());
  }

  @Test
  public void testInMemoryKeyWithParent() {
    HashSet<InMemoryKey> set = new HashSet<InMemoryKey>();
    final InMemoryKey parentKey = new InMemoryKey(null, "parent", "parent");

    InMemoryKey one = new InMemoryKey(parentKey, "one", "one");
    set.add(one);

    InMemoryKey uno = new InMemoryKey(parentKey, "one", "one");
    set.add(uno);
    assertEquals(one, uno);
    assertEquals(1, set.size());

    InMemoryKey two = new InMemoryKey(parentKey, "one", "two");
    set.add(two);
    assertNotEquals(one, two);
    assertEquals(2, set.size());
  }

}
