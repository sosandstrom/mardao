package net.sf.mardao.api.geo.aed;

import com.google.appengine.api.datastore.GeoPt;
import java.util.Collection;

/**
 *
 * @author os
 */
public interface GeoModel {
    Long getId();
    GeoPt getLocation();
    double getLatitude();
    double getLongitude();
    void setGeoboxes(Collection<String> geoboxes);
}
