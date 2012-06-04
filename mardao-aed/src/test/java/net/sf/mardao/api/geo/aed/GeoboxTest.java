/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.geo.aed;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class GeoboxTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(GeoboxTest.class);
    
    public GeoboxTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LOG.info("=== {} setUp() ===", getName());
    }
    
    @Override
    protected void tearDown() throws Exception {
        LOG.info("--- {} tearDown() ---", getName());
        super.tearDown();
    }

    public void testGetCellSE1() {
        assertEquals("(-10,10) 1B", 6L, Geobox.getCell(-10f, 10f, 1));
    }
    public void testGetCellSE2() {
        assertEquals("(-10,10) 2B", 0x6AL, Geobox.getCell(-10f, 10f, 2));
    }
    public void testGetCellSW() {
        assertEquals("(-90,-180) 4B", 0x0000L, Geobox.getCell(-90f, -180f, 4));
    }
    public void testGetCellSE() {
        assertEquals("(-90,180) 4B", 0x5555L, Geobox.getCell(-90f, 180f, 4));
    }
    public void testGetCellNW() {
        assertEquals("(90,-180) 4B", 0xAAAAL, Geobox.getCell(90f, -180f, 4));
    }
    public void testGetCellNE() {
        assertEquals("(90,180) 4B", 0xFFFFL, Geobox.getCell(90f, 180f, 4));
    }
    public void testGetCellOrigo() {
        assertEquals("(0,0) 4B", 0xC000L, Geobox.getCell(0f, 0f, 4));
    }
}
