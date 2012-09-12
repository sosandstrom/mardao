package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.Collection;
import net.sf.mardao.api.CursorPage;
import net.sf.mardao.api.Filter;
import net.sf.mardao.api.domain.PrimaryKeyEntity;

/**
 * 
 * @author os
 * 
 * @param <T>
 *            The domain object type
 * @param <ID>
 *            The domain object's simple key type
 */
public interface Dao<T extends PrimaryKeyEntity<ID>, ID extends Serializable> {
    
    Collection<String> getColumnNames();

    String getParentKeyColumnName();

    String getPrimaryKeyColumnName();

    String getTableName();
    
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
