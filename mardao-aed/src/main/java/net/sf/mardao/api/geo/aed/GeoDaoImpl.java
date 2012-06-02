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

    private static final int RADIUS = 6378135;
    
    private final GeoDao<G> dao;

    public GeoDaoImpl(GeoDao<G> dao) {
        this.dao = dao;
    }

    public Collection<G> findInGeobox(float lat, float lng, int predefinedBox, String orderBy, boolean ascending, int offset, int limit, Expression... filters) {
        return findInGeobox(lat, lng, GEOBOX_CONFIGS[predefinedBox][0], GEOBOX_CONFIGS[predefinedBox][1], orderBy, ascending, offset, limit, filters);     
    }

    public Collection<G> findInGeobox(float lat, float lng, int resolution, int slice, String orderBy, boolean ascending, int offset, int limit, Expression... filters) {
        // FIXME: fishy lat/long order
        final String box = Geobox.compute(lng, lat, resolution, slice);
        
        final Expression geoFilters[] = Arrays.copyOf(filters, filters != null ? filters.length + 1 : 1, Expression[].class);
        geoFilters[geoFilters.length-1] = new FilterEqual(dao.getColumnName(), box);
//        if (filter == null) {
//            filter = "";
//        } else {
//            filter += " && ";
//        }
//        filter += dao.getColumnName() + "=='" + box + "'";        
        return findGeo(orderBy, ascending, offset, limit, geoFilters);
    }

    public Collection<G> findNearest(final float lat, final float lng, String orderBy, boolean ascending, int offset, int limit, Expression... filters) {
        LinkedHashMap<Object, G> uniqueList = new LinkedHashMap<Object, G>();
        int length = offset + limit;
        for (int i = 0; i < GEOBOX_CONFIGS.length; i++) {       
            Collection<G> subList = findInGeobox(lat, lng, i, orderBy, ascending, 0, limit, filters);
            for (G model : subList) {
                uniqueList.put(model.getId(), model);
            }
            if (uniqueList.size() >= length) {
                break;
            }
        }

        List<G> list = new ArrayList<G>();
        int i = 0;
        for (Object key : uniqueList.keySet()) {
            if (i >= offset && i <= length) {
                list.add(uniqueList.get(key));
            }
            i++;
        }

        final GeoPt p = new GeoPt(lat, lng);
        Collections.sort(list, new Comparator<G>() {
            public int compare(G model1, G model2) {                
                double distance1 = distance(model1.getLocation(), p);
                double distance2 = distance(model2.getLocation(), p);
                return Double.compare(distance1, distance2);
            }
        });

        return list;
    }
    
    @Override
    public void save(G model) {
        preStore(model);
        dao.save(model);
    }

    private void preStore(G model) {
        // geoboxes are needed to findGeo the nearest entities and sort them by distance
        Collection<String> geoboxes = new ArrayList<String>();
        for (int[] geobox : GEOBOX_CONFIGS) {
             // use set
             if (geobox[2] == 1) {
                 geoboxes.addAll(Geobox.computeSet(model.getLatitude(), model.getLongitude(), geobox[0], geobox[1]));
             } else {
                 geoboxes.add(Geobox.compute(model.getLatitude(), model.getLongitude(), geobox[0], geobox[1]));
             }
        }
        model.setGeoboxes(geoboxes);
    }

    @Override
    public Collection<G> findGeo(String orderBy, boolean ascending, int offset, int limit, Expression... filters) {
        final Collection<G> list = dao.findGeo(orderBy, ascending, offset, limit, filters);
        
        LOG.debug("findGeo with offset {} and limit {} returns {} entities", new Object[] {offset, limit, list.size()});
        
        return list;
    }

    @Override
    public String getColumnName() {
        return dao.getColumnName();
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