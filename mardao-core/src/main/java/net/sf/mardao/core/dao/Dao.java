package net.sf.mardao.core.dao;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;
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
public interface Dao<T, ID extends Serializable> {
    
    T createDomain(Map<String, String> properties);
    T createDomain(Object primaryKey);
    T createDomain(Object parentKey, ID simpleKey);
    Filter createEqualsFilter(String columnName, Object value);
    Filter createGreaterThanOrEqualFilter(String columnName, Object value);
    Filter createInFilter(String fieldName, Collection param);
    
    Collection<String> getColumnNames();
    
    /**
     * Returns the class of the domain property for specified column
     * @param columnName
     * @return the class of the domain property
     */
    Class getColumnClass(String columnName);
    
    Map<String, Object> getDomainProperties(Object domain);
    
    T getDomain(Future<?> future);

    String getKeyString(Object key);

    String getParentKeyColumnName();

    String getPrimaryKeyColumnName();
    
    ID getSimpleKey(Future<?> future);
    Collection<ID> getSimpleKeys(Future<List<?>> futures);
    ID getSimpleKey(T domain);
    ID getSimpleKey(Map<String, String> properties);
    
    ID getSimpleKeyByPrimaryKey(Object primaryKey);
    
    Collection<ID> getSimpleKeys(Iterable<T> domains);
    
    Object getParentKey(T domain);
    Object getParentKey(Map<String, String> properties);
    
    Object getParentKeyByPrimaryKey(Object primaryKey);
    
    Object getPrimaryKey(String keyString);

    Object getPrimaryKey(T domain);

    Object getPrimaryKey(Object parentKey, ID simpleKey);
    
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
    
    /** Count all entities of this kind */
    int count();
    
    int delete(Object parentKey, Iterable<ID> simpleKeys);
    
    boolean delete(Object parentKey, ID simpleKey);
    
    boolean delete(ID simpleKey);
    
    boolean delete(T domain);
    
    int deleteAll();
    
//    /** memCached queryAll */
//    Collection<T> findAll();
    
    T findByPrimaryKey(Object parentKey, ID simpleKey);
    
    T findByPrimaryKey(ID simpleKey);

    T findByPrimaryKey(Object primaryKey);
    
    Future<?> findByPrimaryKeyForFuture(Object parentKey, ID simpleKey);
    
    Future<?> findByPrimaryKeyForFuture(ID simpleKey);

    Future<?> findByPrimaryKeyForFuture(Object primaryKey);
    
    ID persist(T domain);

    Collection<ID> persist(Iterable<T> domains);
    
    Future<?> persistForFuture(T domain);

    Future<List<?>> persistForFuture(Iterable<T> domains);
    
    Iterable<T> queryAll();
    
    Iterable<T> queryAll(Object parentKey);
    
    Iterable<ID> queryAllKeys();
    
    Iterable<ID> queryAllKeys(Object parentKey);
    
    TreeMap queryByAncestorKey(Object ancestorKey);
    
    Iterable<T> queryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys);
    
    Iterable<T> queryChunk(int offset, int limit, 
            Object ancestorKey, Object primaryKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters);
    
    CursorPage<T> queryPage(int pageSize, String cursorString);

    CursorPage<T> queryPage(int pageSize, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            String cursorString);
    
    void update(Iterable<T> domains);
    
    void update(T domain);
    
    CursorPage<ID> whatsChanged(Date since, int pageSize, String cursorKey);
    
    CursorPage<ID> whatsChanged(Object parentKey, Date since, int pageSize, String cursorKey, Filter... filters);
    
    void writeAsCsv(OutputStream out, String[] columns, CsvConverter<T> converter, Object ancestorKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending, 
            Filter... filters);
    
    void writeAsCsv(OutputStream out, String[] columns, Iterable<T> qi);
    
    void writeAsCsv(OutputStream out, String[] columns, CsvConverter<T> converter, Iterable<T> qi);

    // --- GeoDao methods ---
    
    CursorPage<T> queryInGeobox(float lat, float lng, int bits, int pageSize, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            String cursorString, Filter... filters);

    Collection<T> findNearest(final float lat, final float lng, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            int offset, int limit, Filter... filters);
    
    // --- Transaction methods ---
    
    /**
     * Starts a transaction and returns a generic reference
     * @return a generic reference
     * @since 2.3.1
     */
    Object beginTransaction();
    
    /**
     * Commits a transaction
     * @param transaction a generic reference, as returned by beginTransaction
     * @since 2.3.1
     */
    void commitTransaction(Object transaction);
    
    /**
     * Rollbacks a transaction, if active
     * @param transaction a generic reference, as returned by beginTransaction
     * @since 2.3.1
     */
    void rollbackActiveTransaction(Object transaction);
    
}
