package net.sf.mardao.core.geo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Geobox {

    public static final int RADIUS = 6378135;
    
    static final float[] D_MAJOR = new float[58];
    
    /** 10 bits gives a box of 39km at the Equator */
    public static final int BITS_10_39km = 10;
    
    /** 12 bits gives a box of 9.8km at the Equator */
    public static final int BITS_12_10km = 12;
    
    /** 15 bits gives a box of 1224m at the Equator */
    public static final int BITS_15_1224m = 15;
    
    /** 17 bits gives a box of 307m at the Equator */
    public static final int BITS_17_307m = 17;
    
    /** 18 bits gives a box of 154m at the Equator */
    public static final int BITS_18_154m = 18;
    
    /** 19 bits gives a box of 77.5m at the Equator */
    public static final int BITS_19_77m = 19;
    
    /** 20 bits gives a box of 39m at the Equator */
    public static final int BITS_20_39m = 20;
    
    /** 21 bits gives a box of 19.1m at the Equator */
    public static final int BITS_21_19m = 21;
    
    /** 22 bits gives a box of 10.6m at the Equator */
    public static final int BITS_22_10m = 22;
    
    /** 23 bits gives a box of 5.3m at the Equator */
    public static final int BITS_23_53dm = 23;
    
    /** 24 bits gives a box of 3.2m at the Equator */
    public static final int BITS_24_32dm = 24;
    
    static {
        // initialize floating point once and for all
        double deg = 180.0;
        for (int i = 0; i < D_MAJOR.length; i++) {
            D_MAJOR[i] = (float) deg;
            deg = deg/2;
        }
    }
    
    public static long getMask(float x, float base, int bits) {
        return (long) Math.floor(((x+base)/(2*base)) * (1<<(bits-1)));
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
    
    protected static long getHashRecursive(float major, float minor, int bit, float dMajor, float dMinor) {
        final long hash = major < 0f ? 0L : 1L << bit;
        final float dM = dMajor/2;
        long tail = 0;
        if (0 < bit) {
            tail = getHashRecursive(minor, major + (major < 0 ? dM : -dM), bit-1, dMinor, dM);
        }
//        LOG.info("bit {}, maj {}, dM {} hash 0b{}", new Object[] {
//           Integer.toString(bit), Float.toString(major), Float.toString(dMajor), Long.toBinaryString(hash)});
        return hash | tail;
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
     * 
     * @param lat
     * @param lng
     * @param precision in nibbles (4 bits per nibble)
     * @return 
     */
    public static long getCell(float lat, float lng, int precision) {
        return getCell(lat, lng, 4*precision, 90.0f, 180.0f);
    }
    
    /**
     * 8 c {-10,10} -> 4
     * 0 4
     * then
     * 2 3 {-5,5}
     * 0 1
     * 
     * a b e f
     * 8 9 c d
     * 2 3 6 7
     * 0 1 4 5
     * 
     * @param lat
     * @param lng
     * @param bit
     * @param dy
     * @param dx
     * @return 
     */
    private static long getCell(float lat, float lng, int bit, float dy, float dx) {
        long mx, my;
        float nLat=lat, nLng=lng;
        // 0..3 or 8..B ?
        if (lng < 0) {
            mx = 0;
            // -10 -> 40 (upper half shift up)
            nLng = lng + dx/2;
        }
        else {
            mx = 1 << (bit-2);
            
            // 10 -> -40 (lower half shift down)
            nLng = lng - dx/2;
        }
        
        if (lat < 0) {
            my = 0;
            // -10 -> 35
            nLat = lat + dy/2;
        }
        else {
            my = 1 << (bit-1);
            
            // 10 -> -85 (left half shift left)
            nLat = lat - dy/2;
        }
        
//        LOG.info("getCell=0x{} bit={} lat={} lng={} dy={} dx={} mx=0x{} my=0x{}", new Object[] {
//            Long.toHexString(my+mx), bit, lat, lng, dy, dx,
//            Long.toHexString(mx), Long.toHexString(my)});
        long sub = (2 < bit) ? getCell(nLat, nLng, bit-2, dy/2, dx/2) : 0;
        long returnValue = my +  mx + sub;
        
        return returnValue;
    }

    private static double roundSlicedown(double coord, double slice) {
        double remainder = coord % slice;
        if (remainder == Double.NaN) {
            return coord;
        }
        if (coord > 0) {
            return coord - remainder + slice;
        } else {
            return coord - remainder;
        }
    }

    private static double[] computeTuple(double lat, double lng,
            int resolution, double slice) {
        slice = slice * Math.pow(10, -resolution);
        double adjustedLat = roundSlicedown(lat, slice);
        double adjustedLng = roundSlicedown(lng, slice);
        return new double[] { adjustedLat, adjustedLng - slice,
                adjustedLat - slice, adjustedLng };
    }

    private static String formatTuple(double[] values, int resolution) {
        StringBuffer s = new StringBuffer();
        String format = String.format("%%.%df", resolution);
        for (int i = 0; i < values.length; i++) {
            s.append(String.format(format, values[i]).replace(',','.'));
            if (i < values.length - 1) {
                s.append("|");
            }
        }
        return s.toString();
    }

    public static String compute(double lat, double lng, int resolution,
            int slice) {
        return formatTuple(computeTuple(lat, lng, resolution, slice),
                resolution);
    }

    public static List<String> computeSet(double lat, double lng,
            int resolution, double slice) {
        double[] primaryBox = computeTuple(lat, lng, resolution, slice);
        slice = slice * Math.pow(10, -resolution);
        List<String> set = new ArrayList<String>();
        for (int i = -1; i < 2; i++) {
            double latDelta = slice * i;
            for (int j = -1; j < 2; j++) {
                double lngDelta = slice * j;
                double[] adjustedBox = new double[] { primaryBox[0] + latDelta,
                        primaryBox[1] + lngDelta, primaryBox[2] + latDelta,
                        primaryBox[3] + lngDelta };
                set.add(formatTuple(adjustedBox, resolution));
            }
        }
        return set;
    }

   /**
     * Calculates the great circle distance between two points (law of cosines).
     *
     * @param p1: indicating the first point.
     * @param p2: indicating the second point.
     * @return The 2D great-circle distance between the two given points, in meters.
     */
	public static double distance(DLocation p1, DLocation p2) {
		double p1lat = Math.toRadians(p1.getLatitude());
		double p1lon = Math.toRadians(p1.getLongitude());
		double p2lat = Math.toRadians(p2.getLatitude());
		double p2lon = Math.toRadians(p2.getLongitude());
		return RADIUS
				* Math.acos(makeDoubleInRange(Math.sin(p1lat) * Math.sin(p2lat)
						+ Math.cos(p1lat) * Math.cos(p2lat)
						* Math.cos(p2lon - p1lon)));
	}
        
	/**
	 * This function is used to fix issue 10:
	 * GeocellUtils.distance(...) uses Math.acos(arg) method. In some cases arg > 1 (i.e 1.0000000002), so acos cannot be calculated and the method returns NaN.
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

}