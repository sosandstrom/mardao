package net.sf.mardao.api.geo.aed;

import java.util.Collection;
import net.sf.mardao.api.dao.Expression;

/**
 *
 * @author os
 */
public interface GeoDao<G extends GeoModel> {
    String getColumnName();
    Collection<G> findGeo(String orderBy, boolean ascending, int offset, int limit, Expression... filters);
    void save(G model);
}
