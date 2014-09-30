package net.sf.mardao.core.geo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Geobox {

    public static final int RADIUS = 6378135;
    public static final int D = 40075004;
    
    static final float[] D_MAJOR = new float[58];
    
    /** 10 bits gives a box of 39km at the Equator */
    @Deprecated
    public static final int BITS_10_39km = 10;
    
    /** 12 bits gives a box of 9.8km at the Equator */
    @Deprecated
    public static final int BITS_12_10km = 12;
    
    /** 15 bits gives a box of 1224m at the Equator */
    @Deprecated
    public static final int BITS_15_1224m = 15;
    
    /** 17 bits gives a box of 307m at the Equator */
    @Deprecated
    public static final int BITS_17_307m = 17;
    
    /** 18 bits gives a box of 154m at the Equator */
    @Deprecated
    public static final int BITS_18_154m = 18;
    
    /** 19 bits gives a box of 77.5m at the Equator */
    @Deprecated
    public static final int BITS_19_77m = 19;
    
    /** 20 bits gives a box of 39m at the Equator */
    @Deprecated
    public static final int BITS_20_39m = 20;
    
    /** 22 bits gives a box of 10.6m at the Equator */
    @Deprecated
    public static final int BITS_22_10m = 22;
    
    /** 23 bits gives a box of 5.3m at the Equator */
    @Deprecated
    public static final int BITS_23_53dm = 23;
    
    /** 24 bits gives a box of 3.2m at the Equator */
    @Deprecated
    public static final int BITS_24_32dm = 24;
    
    /** 0 bits gives a box of 40075004m at the Equator */
    public static final int BITS_0_40075004m = 0;

    /** 1 bits gives a box of 20037502m at the Equator */
    public static final int BITS_1_20037502m = 1;

    /** 2 bits gives a box of 10018751m at the Equator */
    public static final int BITS_2_10018751m = 2;

    /** 3 bits gives a box of 5009375m at the Equator */
    public static final int BITS_3_5009375m = 3;

    /** 4 bits gives a box of 2504687m at the Equator */
    public static final int BITS_4_2504687m = 4;

    /** 5 bits gives a box of 1252343m at the Equator */
    public static final int BITS_5_1252343m = 5;

    /** 6 bits gives a box of 626171m at the Equator */
    public static final int BITS_6_626171m = 6;

    /** 7 bits gives a box of 313085m at the Equator */
    public static final int BITS_7_313085m = 7;

    /** 8 bits gives a box of 156542m at the Equator */
    public static final int BITS_8_156542m = 8;

    /** 9 bits gives a box of 78271m at the Equator */
    public static final int BITS_9_78271m = 9;

    /** 10 bits gives a box of 39135m at the Equator */
    public static final int BITS_10_39135m = 10;

    /** 11 bits gives a box of 19567m at the Equator */
    public static final int BITS_11_19567m = 11;

    /** 12 bits gives a box of 9783m at the Equator */
    public static final int BITS_12_9783m = 12;

    /** 13 bits gives a box of 4891m at the Equator */
    public static final int BITS_13_4891m = 13;

    /** 14 bits gives a box of 2445m at the Equator */
    public static final int BITS_14_2445m = 14;

    /** 15 bits gives a box of 1222m at the Equator */
    public static final int BITS_15_1222m = 15;

    /** 16 bits gives a box of 611m at the Equator */
    public static final int BITS_16_611m = 16;

    /** 17 bits gives a box of 305m at the Equator */
    public static final int BITS_17_305m = 17;

    /** 18 bits gives a box of 152m at the Equator */
    public static final int BITS_18_152m = 18;

    /** 19 bits gives a box of 76m at the Equator */
    public static final int BITS_19_76m = 19;

    /** 20 bits gives a box of 38m at the Equator */
    public static final int BITS_20_38m = 20;

    /** 21 bits gives a box of 19m at the Equator */
    public static final int BITS_21_19m = 21;

    /** 22 bits gives a box of 9m at the Equator */
    public static final int BITS_22_9m = 22;

    /** 23 bits gives a box of 4m at the Equator */
    public static final int BITS_23_4m = 23;

    /** 24 bits gives a box of 2m at the Equator */
    public static final int BITS_24_2m = 24;

    /** 25 bits gives a box of 1m at the Equator */
    public static final int BITS_25_1m = 25;

    /** 26 bits gives a box of 0m at the Equator */
    public static final int BITS_26_0m = 26;

    static {
        // initialize floating point once and for all
        double deg = 180.0;
        for (int i = 0; i < D_MAJOR.length; i++) {
            D_MAJOR[i] = (float) deg;
            deg = deg/2;
        }
    }
    
    public static long getMask(float x, float base, int bits) {
        return (long) Math.floor(((x + base) / (2 * base)) * (1 << (bits - 1)));
    }
    
    public static long getHash(float lat, float lng, int bits) {
        if (bits <=0) {
            throw new IllegalArgumentException("bits must be greater than zero");
        }
        if (29 < bits) {
            throw new IllegalArgumentException("bits must not be greater than 29");
        }
        final long precision = ((long)bits) << 59L;
        final long mLat = getMask(lat, 90f, bits);
        final long mLng = getMask(lng, 180f, bits+1);
        final long hash = precision | (mLat<<30L) | mLng;
//        LOG.info("lat 0x{}, long 0x{}, bits 0x" + Long.toHexString(precision), Long.toHexString(mLat), Long.toHexString(mLng));
        
        return hash;
    }
    
    public static Set<Long> getTuple(float lat, float lng, int bits) {
        if (bits <=0) {
            throw new IllegalArgumentException("bits must be greater than zero");
        }
        if (29 < bits) {
            throw new IllegalArgumentException("bits must not be greater than 29");
        }
        final long precision = ((long)bits) << 59L;
        final long mLat = getMask(lat, 90f, bits);
        final long mLng = getMask(lng, 180f, bits+1);
        
        final Set<Long> tuple = new TreeSet<Long>();
        int i = 0;
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                tuple.add(precision | ((mLat+y)<<30L) | (mLng+x));
            }
        }
        
        return tuple;
    }

  protected static long getHashIterative(float major, float minor, final int bits) {
        // initial values,
        // start with -180..180 longitude
        long hash = 0;
        float temp;
        for (int b = bits, i = 0; 0 <= b; b--, i++) {
            temp = minor;

            if (major < 0f) {
                minor = major + D_MAJOR[i+1];
            }
            else {
                hash |= 1L << b;
                minor = major - D_MAJOR[i+1];
            }
//            LOG.debug("bit {}, maj {}, dM {} hash 0b{} 0x{}", new Object[] {
//                Integer.toString(b), Float.toString(major), Float.toString(D_MAJOR[i]), 
//                Long.toBinaryString(hash), Long.toHexString(hash)});
            
            major = temp;
        }
        return hash;
    }

  /**
     * Calculates the great circle distance between two points (law of cosines).
     *
     * @param p1 indicating the first point.
     * @param p2 indicating the second point.
     * @return The 2D great-circle distance between the two given points, in meters.
     */
	public static double distance(DLocation p1, DLocation p2) {
		double p1lat = Math.toRadians(p1.getLatitude());
		double p1lon = Math.toRadians(p1.getLongitude());
		double p2lat = Math.toRadians(p2.getLatitude());
		double p2lon = Math.toRadians(p2.getLongitude());
		return distance(p1lat, p1lon, p2lat, p2lon);
	}
        
	/**
	 * This function is used to fix issue 10:
	 * GeocellUtils.distance(...) uses Math.acos(arg) method. In some cases arg &gt; 1 (i.e 1.0000000002),
   * so acos cannot be calculated and the method returns NaN.
	 * @param d
	 * @return a double between -1 and 1
	 */
	public static double makeDoubleInRange(double d) {
		double result = d;
		if (d > 1) {
			result = 1;
		} else if (d < -1) {
			result = -1;
		}
		return result;
	}
        
        public static int getCellSize(int bits) {
            return D >> bits;
        }
        
        public static double getCellSize(int bits, double latitude) {
            return Math.cos(latitude) * getCellSize(bits);
        }

   /**
     * Calculates the great circle distance between two points (law of cosines).
     *
     * @param p1lat indicating the first point latitude.
     * @param p1lon indicating the first point longitude.
     * @param p2lat indicating the second point latitude
     * @param p2lon indicating the second point longitude
     * @return The 2D great-circle distance between the two given points, in meters.
     */
    public static double distance(double p1lat, double p1lon, double p2lat, double p2lon) {
        return RADIUS
                        * Math.acos(makeDoubleInRange(Math.sin(p1lat) * Math.sin(p2lat)
          + Math.cos(p1lat) * Math.cos(p2lat)
          * Math.cos(p2lon - p1lon)));
    }
}