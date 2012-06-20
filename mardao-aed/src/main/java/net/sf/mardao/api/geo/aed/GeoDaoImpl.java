package net.sf.mardao.api.geo.aed;

import com.google.appengine.api.datastore.GeoPt;
import java.util.*;
import net.sf.mardao.api.dao.Expression;
import net.sf.mardao.api.dao.FilterEqual;
import net.sf.mardao.api.domain.AEDPrimaryKeyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoDaoImpl<T extends AEDPrimaryKeyEntity<Long>, G extends GeoModel> implements GeoDao<G> {
    static final Logger LOG = LoggerFactory.getLogger(GeoDaoImpl.class);

    // geobox configs are: resolution, slice, use set (1 = true)
    private final static int[][] GEOBOX_CONFIGS = 
        { { 4, 5, 1 },
          { 3, 2, 1 },
          { 3, 8, 0 },
          { 3, 16, 0 },
          { 2, 5, 0 } };
    
    private static List<Integer> boxBits = Arrays.asList(
        Geobox.BITS_12_10km, Geobox.BITS_15_1224m, Geobox.BITS_18_154m
    );

    private static final int RADIUS = 6378135;
    
    private final GeoDao<G> dao;

    public GeoDaoImpl(GeoDao<G> dao) {
        this.dao = dao;
    }

    public Collection<G> findInGeobox(float lat, float lng, int bits, String orderBy, boolean ascending, int offset, int limit, Expression... filters) {
        if (!boxBits.contains(bits)) {
            throw new IllegalArgumentException("Unboxed resolution, hashed are " + boxBits);
        }
        
        final long box = Geobox.getHash(lat, lng, bits);
        
        final Expression geoFilters[] = Arrays.copyOf(filters, filters != null ? filters.length + 1 : 1, Expression[].class);
        geoFilters[geoFilters.length-1] = new FilterEqual(dao.getGeoboxesColumnName(), box);
        return findGeoBase(orderBy, ascending, limit, offset, geoFilters);
    }

//    public Collection<G> findInGeobox(float lat, float lng, int resolution, int slice, String orderBy, boolean ascending, int offset, int limit, Expression... filters) {
//        // FIXME: fishy lat/long order
//        final String box = Geobox.compute(lng, lat, resolution, slice);
//        
//        final Expression geoFilters[] = Arrays.copyOf(filters, filters != null ? filters.length + 1 : 1, Expression[].class);
//        geoFilters[geoFilters.length-1] = new FilterEqual(dao.getGeoboxesColumnName(), box);
////        if (filter == null) {
////            filter = "";
////        } else {
////            filter += " && ";
////        }
////        filter += dao.getGeoboxesColumnName() + "=='" + box + "'";        
//        return findGeoBase(orderBy, ascending, limit, offset, geoFilters);
//    }
//
    public Collection<G> findNearest(final float lat, final float lng, String orderBy, boolean ascending, int offset, int limit, Expression... filters) {
        final GeoPt p = new GeoPt(lat, lng);
        int size = offset + (0 < limit ? limit : 10000);
        
        // sorting on distance has to be done outside datastore, i.e. here in application:
        Map<Double, G> orderedMap = new TreeMap<Double, G>();
        for (int bits : boxBits) {       
            final Collection<G> subList = findInGeobox(lat, lng, bits, orderBy, ascending, 0, limit, filters);
            for (G model : subList) {
                double d = distance(model.getLocation(), p);
                orderedMap.put(d, model);
            }
            
            if (size <= orderedMap.size()) {
                break;
            }
        }
        // return with specified offset and limit
        final Collection<G> values = orderedMap.values();
        G[] page = (G[]) Arrays.copyOfRange(values.toArray(), 
                Math.min(offset, values.size()), Math.min(size, values.size()));
        return Arrays.asList(page);
    }

    /** returns the configured resolutions for calculating boxes,
     * defaults to 12, 15 and 18 bits (10km, 1224m and 154m)
     */
    public static List<Integer> getBoxBits() {
        return boxBits;
    }

    public static void setBoxBits(List<Integer> boxBits) {
        GeoDaoImpl.boxBits = boxBits;
    }
    
    
    
    @Override
    public Long save(G model) {
        preStore(model);
        return dao.save(model);
    }

    /**
     * populates the GeoModel with nearby boxes for configured resolutions
     * @param model 
     */
    protected static void preStore(GeoModel model) {
        
        // geoboxes are needed to findGeo the nearest entities and sort them by distance
        Collection<Long> geoboxes = new ArrayList<Long>();
        for (int bits : boxBits) {
            geoboxes.addAll(Geobox.getTuple(model.getLatitude(), model.getLongitude(), bits));
        }
        model.setGeoboxes(geoboxes);
    }

    @Override
    public Collection<G> findGeoBase(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        final Collection<G> list = dao.findGeoBase(orderBy, ascending, limit, offset, filters);
        
        LOG.debug("findGeo with offset {} and limit {} returns {} entities", new Object[] {offset, limit, list.size()});
        
        return list;
    }

    @Override
    public String getGeoboxesColumnName() {
        return dao.getGeoboxesColumnName();
    }

    /**
     * Calculates the great circle distance between two points (law of cosines).
     *
     * @param p1: indicating the first point.
     * @param p2: indicating the second point.
     * @return The 2D great-circle distance between the two given points, in meters.
     */
	public static double distance(GeoPt p1, GeoPt p2) {
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