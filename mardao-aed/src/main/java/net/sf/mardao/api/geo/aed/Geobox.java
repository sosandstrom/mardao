package net.sf.mardao.api.geo.aed;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Geobox {
    static final Logger LOG = LoggerFactory.getLogger(Geobox.class);
    
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
        
        LOG.info("getCell=0x{} bit={} lat={} lng={} dy={} dx={} mx=0x{} my=0x{}", new Object[] {
            Long.toHexString(my+mx), bit, lat, lng, dy, dx,
            Long.toHexString(mx), Long.toHexString(my)});
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

}