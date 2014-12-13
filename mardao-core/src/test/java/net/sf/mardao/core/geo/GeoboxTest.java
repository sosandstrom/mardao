/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author os
 */
public class GeoboxTest {
//    static final Logger LOG = LoggerFactory.getLogger(GeoboxTest.class);

  @Before
  public void setUp() throws Exception {
//        LOG.info("=== {} setUp() ===", getName());
  }

  @After
  public void tearDown() throws Exception {
//        LOG.info("--- {} tearDown() ---", getName());
  }

  @Test
  public void testGetHashN() {
    assertEquals("E, 1 bit", 1L, Geobox.getHashIterative(120f, -45f, 0));
  }

  @Test
  public void testGetHashNE() {
    assertEquals("NE, 2 bit", 3L, Geobox.getHashIterative(120f, 45f, 1));
  }

  @Test
  public void testGetHashOrigo() {
    assertEquals("(0,0), 16 bit", 0xC000L, Geobox.getHashIterative(0f, 0f, 15));
  }

  @Test
  public void testGetHashNEedge() {
    assertEquals("(90,180), 16 bit", 0xFFFFL, Geobox.getHashIterative(180f, 90f, 15));
  }

  @Test
  public void testGetHashSWedge() {
    assertEquals("(-90,-180), 16 bit", 0x0000L, Geobox.getHashIterative(-180f, -90f, 15));
  }

  public void testGetMask() {
//        LOG.info("mask for -179 is 0x{}", Long.toHexString(Geobox.getMask(-179f, 180f, 16)));
//        LOG.info("mask for -1 is 0x{}", Long.toHexString(Geobox.getMask(-1f, 180f, 16)));
//        LOG.info("mask for 0 is 0x{}", Long.toHexString(Geobox.getMask(0f, 180f, 16)));
//        LOG.info("mask for 1 is 0x{}", Long.toHexString(Geobox.getMask(1f, 180f, 16)));
//        LOG.info("mask for 179 is 0x{}", Long.toHexString(Geobox.getMask(179f, 180f, 16)));
  }

  public void testGetHash() {
//        LOG.info("hash for (-89,-179) is 0x{}", Long.toHexString(Geobox.getHash(-89.99f, -179.99f, 28)));
//        LOG.info("hash for (0,0) is 0x{}", Long.toHexString(Geobox.getHash(0f, 0f, 28)));
//        LOG.info("hash for (89,179) is 0x{}", Long.toHexString(Geobox.getHash(89.999988f, 179.99995f, 28)));
  }

  @Test
  public void testGetDistance() {
    final DLocation P0 = new DLocation(56.34f, 15.05f);
    final DLocation P1 = new DLocation(56.387f, 15.05f);
    final DLocation P2 = new DLocation(56.3445f, 15.05f);
    final DLocation P3 = new DLocation(56.34043f, 15.05f);
    final DLocation P4 = new DLocation(56.34001f, 15.05f);
    assertEquals(5232.0f, Geobox.distance(P0, P1), 0.2f);
  }

//    public void testBits() {
//        for (int b = 18; b < 27; b++) {
//            LOG.info("bits {}, x1 {} x2 {}", new Object[] {
//                b, Geobox.getMask(56.34f, 90f, b), Geobox.getMask(56.34001f, 90f, b)
//            });
//        }
//    }

  @Test
  public void testSize() {
    final float lat = 25.18f;
    for (int bits = 10; bits < 25; bits++) {
      final long maskLat = Geobox.getMask(lat, 90f, bits);

      // decrease until limit
      long m = maskLat;
      float latMin = lat;
      while (m == maskLat) {
        latMin -= 0.00001f;
        m = Geobox.getMask(latMin, 90f, bits);
      }
//            LOG.info("latMin = {}", latMin);


      // increase until limit
      m = maskLat;
      float latMax = lat;
      while (m == maskLat) {
        latMax += 0.00001f;
        m = Geobox.getMask(latMax, 90f, bits);
      }
//            LOG.info("latMax = {}", latMax);

      DLocation pMin = new DLocation(latMin, lat);
      DLocation pMax = new DLocation(latMax, lat);
//            LOG.info("lat distance for {} bits is {}m", bits, Geobox.distance(pMin, pMax));

      pMin = new DLocation(lat, latMin);
      pMax = new DLocation(lat, latMax);
//            LOG.info("long distance at N{} is {}m", lat, GeoDaoImpl.distance(pMin, pMax));
    }
  }

  @Test
  public void testTuple() {
//        LOG.info("tuple20={}", Geobox.getTuple(56.34f, 15.05f, Geobox.BITS_20_39m));
//        LOG.info("tuple22={}", Geobox.getTuple(56.34f, 15.05f, Geobox.BITS_22_10m));
//        LOG.info("tuple23={}", Geobox.getTuple(56.34f, 15.05f, Geobox.BITS_23_53dm));

    final long hash = Geobox.getHash(55.6030006409f, 13.0010004044f, Geobox.BITS_12_10km);
//        LOG.info("geoHash 12 = {}", hash);
    Set<Long> boxes = Geobox.getTuple(55.6030006409f, 13.0010004044f, Geobox.BITS_12_10km);
//        LOG.info("tuple 12 = {}", boxes);
    assertTrue("TupleContains", boxes.contains(hash));
  }

}
