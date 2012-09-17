package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.geo.DLocation;

/**
 * 
 * @author os
 * 
 * @param <T>
 *            The domain object type
 * @param <ID>
 *            The domain object's simple key type
 */
public interface Dao<T extends Object, ID extends Serializable> {
    
    Collection<String> getColumnNames();

    String getParentKeyColumnName();

    String getPrimaryKeyColumnName();
    
    ID getSimpleKey(T domain);
    
    Object getParentKey(T domain);
    
    Object getPrimaryKey(T domain);
    
    String getTableName();
    
    void setSimpleKey(T domain, ID simpleKey);
    
    void setParentKey(T domain, Object parentKey);
    
    String getCreatedByColumnName();
    
    String getCreatedDateColumnName();
    
    String getUpdatedByColumnName();
    
    String getUpdatedDateColumnName();

    String getGeoLocationColumnName();
    
    String getCreatedBy(T domain);
    
    Date getCreatedDate(T domain);
    
    String getUpdatedBy(T domain);
    
    Date getUpdatedDate(T domain);

    DLocation getGeoLocation(T domain);
    
    void _setCreatedBy(T domain, String creator);
    
    void _setCreatedDate(T domain, Date date);
    
    void _setUpdatedBy(T domain, String updator);
    
    void _setUpdatedDate(T domain, Date date);
    
    // --- Generic Dao methods ---
    
    int delete(Object parentKey, Iterable<ID> simpleKeys);
    
    boolean delete(Object parentKey, ID simpleKey);
    
    boolean delete(ID simpleKey);
    
    boolean delete(T domain);
    
    T findByPrimaryKey(Object parentKey, ID simpleKey);
    
    T findByPrimaryKey(ID simpleKey);

    ID persist(T domain);

    Collection<ID> persist(Iterable<T> domains);
    
    Iterable<T> queryAll();
    
    Iterable<T> queryAll(Object parentKey);
    
    Iterable<ID> queryAllKeys();
    
    Iterable<ID> queryAllKeys(Object parentKey);
    
    Iterable<T> queryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys);
    
    void update(Iterable<T> domains);
    
    void update(T domain);

    // --- GeoDao methods ---
    
    CursorPage<T, ID> queryInGeobox(float lat, float lng, int bits, int pageSize, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            Serializable cursorString, Filter... filters);

    Collection<T> findNearest(final float lat, final float lng, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            int offset, int limit, Filter... filters);
}
