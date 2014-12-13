package net.sf.mardao.core.geo;

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

import org.junit.Test;

/**
 * @author osandstrom Date: 2014-09-30 Time: 19:58
 */
public class DLocationTest {

  @Test
  public void testSetGetLatitude() {
    DLocation actual = new DLocation();
    float f = 55.5f;
    actual.setLatitude(f);
    assertEquals(f, actual.getLatitude(), 0.1f);
  }

  @Test
  public void testSetGetLongitude() {
    DLocation actual = new DLocation();
    float f = 155.5f;
    actual.setLongitude(f);
    assertEquals(f, actual.getLongitude(), 0.1f);
  }
}
