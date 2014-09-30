package net.sf.mardao.core.geo;

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
