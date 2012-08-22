package net.sf.mardao.api.geo.aed;

import java.io.Serializable;
import java.util.Collection;
import net.sf.mardao.api.dao.Expression;

/**
 *
 * @author os
 */
public interface GeoDao<G extends GeoModel, ID extends Serializable> {
    String getGeoboxesColumnName();
    Collection<G> findGeoBase(String orderBy, boolean ascending, int limit, int offset, Expression... filters);
    Collection<G> findInGeobox(float lat, float lng, int bits, String orderBy, boolean ascending, int offset, int limit, Expression... filters);
    ID save(G model);
}
