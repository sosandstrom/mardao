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

    InMemoryKey one = new InMemoryKey("one", "one");
    set.add(one);

    InMemoryKey uno = new InMemoryKey("one", "one");
    set.add(uno);
    assertEquals(one, uno);
    assertEquals(1, set.size());

    InMemoryKey two = new InMemoryKey("one", "two");
    set.add(two);
    assertNotEquals(one, two);
    assertEquals(2, set.size());
  }

}
