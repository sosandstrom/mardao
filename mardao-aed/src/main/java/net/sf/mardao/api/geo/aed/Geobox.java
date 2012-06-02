package net.sf.mardao.api.geo.aed;

import java.util.ArrayList;
import java.util.List;

public class Geobox {

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