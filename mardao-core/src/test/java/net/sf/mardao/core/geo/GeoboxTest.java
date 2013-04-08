/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.core.geo;

import net.sf.mardao.core.geo.DLocation;
import net.sf.mardao.core.geo.Geobox;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author os
 */
public class GeoboxTest extends TestCase {
//    static final Logger LOG = LoggerFactory.getLogger(GeoboxTest.class);
    
    public GeoboxTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
//        LOG.info("=== {} setUp() ===", getName());
    }
    
    @Override
    protected void tearDown() throws Exception {
//        LOG.info("--- {} tearDown() ---", getName());
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
    
    public void testGetDistance() {
        final DLocation P0 = new DLocation(56.34f, 15.05f);
        final DLocation P1 = new DLocation(56.387f, 15.05f);
        final DLocation P2 = new DLocation(56.3445f, 15.05f);
        final DLocation P3 = new DLocation(56.34043f, 15.05f);
        final DLocation P4 = new DLocation(56.34001f, 15.05f);
//        LOG.info("0.047 = {}m", Geobox.distance(P0, P1));
//        LOG.info("0.0045 = {}m", Geobox.distance(P0, P2));
//        LOG.info("0.00043 = {}m", Geobox.distance(P0, P3));
//        LOG.info("56.34000..56.34001 (0.00001) = {}", Geobox.distance(P0, P4));
    }

//    public void testBits() {
//        for (int b = 18; b < 27; b++) {
//            LOG.info("bits {}, x1 {} x2 {}", new Object[] {
//                b, Geobox.getMask(56.34f, 90f, b), Geobox.getMask(56.34001f, 90f, b)
//            });
//        }
//    }
    
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
