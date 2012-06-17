/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.geo.aed;

import com.google.appengine.api.datastore.GeoPt;
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
    
    public void testGetHashN() {
        assertEquals("E, 1 bit", 1L, Geobox.getHashIterative(120f, -45f, 0));
    }

    public void testGetHashNE() {
        assertEquals("NE, 2 bit", 3L, Geobox.getHashIterative(120f, 45f, 1));
    }

    public void testGetHashOrigo() {
        assertEquals("(0,0), 16 bit", 0xC000L, Geobox.getHashIterative(0f, 0f, 15));
    }

    public void testGetHashNEedge() {
        assertEquals("(90,180), 16 bit", 0xFFFFL, Geobox.getHashIterative(180f, 90f, 15));
    }

    public void testGetHashSWedge() {
        assertEquals("(-90,-180), 16 bit", 0x0000L, Geobox.getHashIterative(-180f, -90f, 15));
    }
    
    public void testGetMask() {
        LOG.info("mask for -179 is 0x{}", Long.toHexString(Geobox.getMask(-179f, 180f, 16)));
        LOG.info("mask for -1 is 0x{}", Long.toHexString(Geobox.getMask(-1f, 180f, 16)));
        LOG.info("mask for 0 is 0x{}", Long.toHexString(Geobox.getMask(0f, 180f, 16)));
        LOG.info("mask for 1 is 0x{}", Long.toHexString(Geobox.getMask(1f, 180f, 16)));
        LOG.info("mask for 179 is 0x{}", Long.toHexString(Geobox.getMask(179f, 180f, 16)));
    }
    
    public void testGetHash() {
        LOG.info("hash for (-89,-179) is 0x{}", Long.toHexString(Geobox.getHash(-89.99f, -179.99f, 28)));
        LOG.info("hash for (0,0) is 0x{}", Long.toHexString(Geobox.getHash(0f, 0f, 28)));
        LOG.info("hash for (89,179) is 0x{}", Long.toHexString(Geobox.getHash(89.999988f, 179.99995f, 28)));
    }
    
    public void testGetDistance() {
        final GeoPt P0 = new GeoPt(56.34f, 15.05f);
        final GeoPt P1 = new GeoPt(56.387f, 15.05f);
        final GeoPt P2 = new GeoPt(56.3445f, 15.05f);
        final GeoPt P3 = new GeoPt(56.34043f, 15.05f);
        final GeoPt P4 = new GeoPt(56.34001f, 15.05f);
        LOG.info("0.047 = {}m", GeoDaoImpl.distance(P0, P1));
        LOG.info("0.0045 = {}m", GeoDaoImpl.distance(P0, P2));
        LOG.info("0.00043 = {}m", GeoDaoImpl.distance(P0, P3));
        LOG.info("56.34000..56.34001 (0.00001) = {}", GeoDaoImpl.distance(P0, P4));
    }

//    public void testBits() {
//        for (int b = 18; b < 27; b++) {
//            LOG.info("bits {}, x1 {} x2 {}", new Object[] {
//                b, Geobox.getMask(56.34f, 90f, b), Geobox.getMask(56.34001f, 90f, b)
//            });
//        }
//    }
    
    public void testSize() {
        for (int b = 0; b < 50; b++) {
            LOG.info("b {}, x1 {}", new Object[] {
                b, 
                Geobox.getMask(56.34f + ((float)b*0.00001f), 90f, Geobox.BITS_22_94dm) 
            });
        }
    }
    
    public void testTuple() {
        LOG.info("tuple20={}", Geobox.getTuple(56.34f, 15.05f, Geobox.BITS_20_38m));
        LOG.info("tuple22={}", Geobox.getTuple(56.34f, 15.05f, Geobox.BITS_22_94dm));
        LOG.info("tuple23={}", Geobox.getTuple(56.34f, 15.05f, Geobox.BITS_23_47dm));
    }
    
//    public void testGetHashMatrix4x4() {
//        final int N = 2;
//        final float DY = 90f/N;
//        final float DX = 180f/N;
//        final Long v[] = new Long[N*2];
//        final String s[] = new String[N*2];
//        for (int y = N-1; -N <= y; y--) {
//            for (int x = -N; x < N; x++) {
//                v[x+N] = Geobox.getHashIterative(DX*(x+0.01f), DY*(y+0.01f), N+1);
//                s[x+N] = Long.toBinaryString(v[x+N]);
//            }
//            LOG.info("{}\t{}\t{}\t{}", s);
//        }
//    }
//
//    public void testGetHashMatrix8x8() {
//        final int N = 4;
//        final float DY = 90f/N;
//        final float DX = 180f/N;
//        final Long v[] = new Long[N*2];
//        final String s[] = new String[N*2];
//        for (int y = N-1; -N <= y; y--) {
//            for (int x = -N; x < N; x++) {
//                v[x+N] = Geobox.getHashIterative(DX*(x+0.01f), DY*(y+0.01f), N+1);
//                s[x+N] = Long.toBinaryString(v[x+N]);
//            }
//            LOG.info("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}", s);
//        }
//    }
//
//    public void testGetHashMatrix16x16() {
//        final int N = 8;
//        final float DY = 90f/N;
//        final float DX = 180f/N;
//        final Long v[] = new Long[N*2];
//        final String s[] = new String[N*2];
//        for (int y = N-1; -N <= y; y--) {
//            for (int x = -N; x < N; x++) {
//                v[x+N] = Geobox.getHashIterative(DX*(x+0.01f), DY*(y+0.01f), 7);
//                s[x+N] = Long.toBinaryString(v[x+N]);
//            }
//            LOG.info("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}", s);
//        }
//    }
//
    /*
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
    */
}
