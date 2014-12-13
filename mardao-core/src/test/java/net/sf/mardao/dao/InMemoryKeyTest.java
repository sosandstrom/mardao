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
