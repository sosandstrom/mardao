package net.sf.mardao.api.geo.aed;

import com.google.appengine.api.datastore.GeoPt;
import java.util.Collection;

/**
 *
 * @author os
 */
public interface GeoModel {
    GeoPt getLocation();
    float getLatitude();
    float getLongitude();
    void setGeoboxes(Collection<Long> geoboxes);
}
