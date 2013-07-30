package net.sf.mardao.core.dao;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.MardaoListFuture;
import net.sf.mardao.core.geo.DLocation;
import net.sf.mardao.core.geo.Geobox;

/**
 * This is the base class for all implementations of the Dao Bean.
 * 
 * @author os
 * 
 * @param <T>
 *            domain object type
 * @param <ID>
 *            domain object simple key type
 * @param <P>
 *            domain object parent key type
 * @param <CT>
 *            cursor type
 * @param <E>
 *            database core entity type
 * @param <C>
 *            database core key type
 */
public abstract class DaoImpl<T, ID extends Serializable, 
        P extends Serializable, CT extends Object,
        E, C extends Serializable>
        implements Dao<T, ID>, CsvConverter<T> {
    
    public static final String PRINCIPAL_NAME_ANONYMOUS = "[ANONYMOUS]";
    
    /** Default name of the geoboxes column is "geoboxes" */
    public static final String COLUMN_NAME_GEOBOXES_DEFAULT = "geoboxes";

    protected static final String AUDIT_CURSOR_PREFIX = "audit-";
    
    private Collection<Integer> boxBits = Arrays.asList(
        Geobox.BITS_18_152m, Geobox.BITS_15_1222m, Geobox.BITS_12_9783m
    );

    /** set this, to have createdBy and updatedBy set */
    private static final ThreadLocal<String> principalName = new ThreadLocal<String>();
    
    /** mostly for logging */
    protected final Class<T> persistentClass;
    
    /** To help converting keys */
    protected final Class<ID> simpleIdClass;
    
    protected DaoImpl mardaoParentDao;
    
    protected static final Runnable RUNNABLE_VOID = new Runnable() {
        public void run() {
        }
    };
    
    protected static final HashMap<String, DaoImpl> DAO_MAP = new HashMap<String, DaoImpl>();
    
    /** to list the property names for ManyToOne relations */
    protected List<String> getBasicColumnNames() {
        return Collections.EMPTY_LIST;
    }
    /** to list the property names for ManyToOne relations */
    protected List<String> getManyToOneColumnNames() {
        return Collections.EMPTY_LIST;
    }
    /** to map the Daos for ManyToOne relations */
    protected DaoImpl getManyToOneDao(String columnName) {
        return null;
    }
    
    /** 
     * Set this to true in DaoBean constructor, to enable
     * the all-domains memCache
     */
    protected boolean memCacheAll = false;

    /** 
     * Set this to true in DaoBean constructor, to enable
     * the primaryKey-to-domain memCache
     */
    protected boolean memCacheEntities = false;
    
    private static Cache memCache = null;
    
    /** inject to get different behavior */
    private static Map memCacheConfig = Collections.EMPTY_MAP;
    
    /**
     * Set this to false in DaoBean constructor, to disable the count()
     * query for first page (cursorKey is null).
     */
    protected boolean populateTotalSize = true;

    protected DaoImpl(Class<T> domainType, Class<ID> simpleIdType) {
        this.persistentClass = domainType;
        this.simpleIdClass = simpleIdType;
        DAO_MAP.put(getTableName(), this);
    }

    public String getTableName() {
        return persistentClass.getSimpleName();
    }

    // --- BEGIN persistence-type beans must implement these ---
    
    /**
     * Implement / Override this in TypeDaoImpl. This method does not have to
     * worry about invalidating the cache, that is done in delete(parentKey, simpleKeys)
     * @param parentKey
     * @param simpleKeys
     * @return number of rows deleted (optional)
     */
    protected abstract int doDelete(Object parentKey, Iterable<ID> simpleKeys);
    /**
     * Implement / Override this in TypeDaoImpl. This method does not have to
     * worry about invalidating the cache, that is done in delete(domains)
     * @param domains
     * @return number of rows deleted (optional)
     */
    protected abstract int doDelete(Iterable<T> domains);
    
    protected abstract T doFindByPrimaryKey(Object parentKey, ID simpleKeys);
    protected abstract Future<?> doFindByPrimaryKeyForFuture(Object parentKey, ID simpleKeys);
    protected abstract Future<List<C>> doPersistCoreForFuture(Iterable<E> entities);
    protected abstract Future<?> doPersistCoreForFuture(E core);
    protected abstract Iterable<T> doQueryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys);
    
    protected abstract T findUniqueBy(Filter... filters);
    protected abstract ID findUniqueKeyBy(Filter... filters);
    
    /** Implemented in TypeDaoImpl */
    protected abstract Collection<C> persistCore(Iterable<E> itrbl);
    
    /** Implemented in TypeDaoImpl */
    protected abstract CursorPage<T> queryPage(boolean keysOnly, int pageSize,
            Object ancestorKey, Object primaryKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            String cursorString,
            Filter... filters);

    /** Implemented in TypeDaoImpl */
    protected abstract Iterable<T> queryIterable(boolean keysOnly, 
            int offset, int limit,
            Object ancestorKey, Object primaryKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters);

    /** Implemented in TypeDaoImpl */
    protected abstract Iterable<ID> queryIterableKeys(int offset, int limit,
            Object ancestorKey, Object primaryKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            Filter... filters);

    /** Implemented in TypeDaoImpl */
    protected abstract ID coreToSimpleKey(E core);
    /** Implemented in TypeDaoImpl */
    protected abstract ID coreKeyToSimpleKey(C core);
    /** Implemented in TypeDaoImpl */
    protected abstract P coreToParentKey(E core);
    /** Implemented in TypeDaoImpl */
    protected abstract P coreKeyToParentKey(C core);
    
    protected abstract int count(Object ancestorKey, Object simpleKey,
            Filter... filters);
    
    protected abstract E createCore(Object primaryKey);
    protected abstract E createCore(Object parentKey, ID simpleKey);
    protected abstract C createCoreKey(Object parentKey, ID simpleKey);    
    
    protected abstract String createMemCacheKey(Object parentKey, ID simpleKey);

    /** Implemented in TypeDaoImpl */
    protected abstract Object getCoreProperty(E core, String name, Class domainPropertyClass);
    /** Implemented in TypeDaoImpl */
    protected abstract void setCoreProperty(Object core, String name, Object value);

    protected abstract void setDomainStringProperty(T domain, String name, Map<String, String> properties);
    
    protected abstract CursorPage<ID> whatsDeleted(Date since, int pageSize, String cursorKey);
    
    // --- END persistence-type beans must implement these ---

    // --- BEGIN Logging interface ---
    
    /** Override this method to do other than System.out.println() */
    protected void println(int priority, String format, Object... args) {
        System.out.println(String.format("%s: %s", getTableName(),
                String.format(format, args)));
    }
    /** Override this method to do other than System.out.println() */
    protected void printStackTrace(int priority, String message, Throwable t) {
        System.out.println(String.format("%s: %s: %s", getTableName(),
                message, t));
    }
    protected void debug(String message, Throwable t) {
        printStackTrace(3, message, t);
    }
    protected void debug(String format, Object... args) {
        println(3, format, args);
    }
    protected void error(String message, Throwable t) {
        printStackTrace(6, message, t);
    }
    protected void error(String format, Object... args) {
        println(6, format, args);
    }
    protected void info(String message, Throwable t) {
        printStackTrace(4, message, t);
    }
    protected void info(String format, Object... args) {
        println(4, format, args);
    }
    protected void warn(String message, Throwable t) {
        printStackTrace(5, message, t);
    }
    protected void warn(String format, Object... args) {
        println(5, format, args);
    }
    
    // --- END Logging interface ---
    
    public T createDomain(Map<String, String> properties) {
        if (null == properties) {
            return null;
        }
        
        final ID simpleKey = getSimpleKey(properties);
        final P parentKey = (P) getParentKey(properties);
        
        final T domain = createDomain(parentKey, simpleKey);

        // created, updated
        setDomainStringProperty(domain, getCreatedByColumnName(), properties);
        setDomainStringProperty(domain, getCreatedDateColumnName(), properties);
        setDomainStringProperty(domain, getUpdatedByColumnName(), properties);
        setDomainStringProperty(domain, getUpdatedDateColumnName(), properties);

        // Domain Entity-specific properties
        for (String name : getColumnNames()) {
            setDomainStringProperty(domain, name, properties);
        }

        return domain;
    }
    
    public T coreToDomain(E core) {
        if (null == core) {
            return null;
        }
        
        final ID simpleKey = coreToSimpleKey(core);
        final P parentKey = coreToParentKey(core);
        
        final T domain = createDomain(parentKey, simpleKey);

        // created, updated
        copyCorePropertyToDomain(getCreatedByColumnName(), core, domain);
        copyCorePropertyToDomain(getCreatedDateColumnName(), core, domain);
        copyCorePropertyToDomain(getUpdatedByColumnName(), core, domain);
        copyCorePropertyToDomain(getUpdatedDateColumnName(), core, domain);

        // Domain Entity-specific properties
        for (String name : getColumnNames()) {
            copyCorePropertyToDomain(name, core, domain);
        }

        return domain;
    }
    
    protected Object copyCorePropertyToDomain(String name, E core, T domain) {
        if (null == name) {
            return null;
        }
        
        Object value = getCoreProperty(core, name, getColumnClass(name));
        setDomainProperty(domain, name, value);
        
        return value;
    }
    
    protected Object copyDomainPropertyToCore(String name, T domain, E core) {
        if (null == name) {
            return null;
        }
        
        Object value = getDomainProperty(domain, name);
        setCoreProperty(core, name, value);
        
        return value;
    }
    
    public T createDomain() throws InstantiationException, IllegalAccessException {
        return createDomain(null, null);
    }

    @Override
    public T createDomain(Object primaryKey) {
        C pk = (C) primaryKey;
        Object parentKey = coreKeyToParentKey(pk);
        ID simpleKey = coreKeyToSimpleKey(pk);
        return createDomain(parentKey, simpleKey);
    }

    @Override
    public T createDomain(Object parentKey, ID simpleKey) {
        try {
            final T domain = persistentClass.newInstance();

            setParentKey(domain, parentKey);
            setSimpleKey(domain, simpleKey);

            return domain;
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException("Access", ex);
        }
        catch (InstantiationException ex) {
            throw new RuntimeException("Access", ex);
        }
    }
    
    public E domainToCore(T domain, final Date currentDate) {
        if (null == domain) {
            return null;
        }
        
        final ID simpleKey = getSimpleKey(domain);
        E core = createCore(getParentKey(domain), simpleKey);
        
        // created, updated
        String principal = getCreatedBy(domain);
        if (null == principal) {
            principal = getPrincipalName();
            if (null == principal) {
                principal = PRINCIPAL_NAME_ANONYMOUS;
            }
            _setCreatedBy(domain, principal);
        }
        setCoreProperty(core, getCreatedByColumnName(), principal);
        
        // if generating id, overwrite createdDate
        Date date = getCreatedDate(domain);
        if (null == date || null == simpleKey) {
            date = currentDate;
            _setCreatedDate(domain, currentDate);
        }
        setCoreProperty(core, getCreatedDateColumnName(), date);
        
        principal = getPrincipalName();
        if (null == principal) {
            principal = PRINCIPAL_NAME_ANONYMOUS;
        }
        _setUpdatedBy(domain, principal);
        setCoreProperty(core, getUpdatedByColumnName(), principal);
        
        _setUpdatedDate(domain, currentDate);
        setCoreProperty(core, getUpdatedDateColumnName(), currentDate);
        
        // Domain Entity-specific properties
        for (String name : getColumnNames()) {
            copyDomainPropertyToCore(name, domain, core);
        }

        // geoboxes
        if (null != getGeoLocationColumnName()) {
            updateGeoModel(domain, core);
        }

        return core;
    }
    
    protected Iterable<E> doQueryByAncestorKey(C ancestorKey) {
        throw new UnsupportedOperationException("Not supported by Database type.");
    }
    
    protected String getCoreKind(E core) {
        throw new UnsupportedOperationException("Not supported by Database type.");
    }
    
    public Map<String, Object> getDomainProperties(Object domainObject) {
        if (null == domainObject) {
            return null;
        }
        
        final T domain = (T) domainObject;
        final Map<String, Object> props = new TreeMap<String, Object>();
        final ID simpleKey = getSimpleKey(domain);
        setMapProperty(props, getPrimaryKeyColumnName(), simpleKey);
        final Object parentKey = getParentKey(domain);
        setMapProperty(props, getParentKeyColumnName(), parentKey);
        
        for (String col : getColumnNames()) {
            Object value = getDomainProperty(domain, col);
            setMapProperty(props, col, value);
        }
        
        return props;
    }
    
    public Collection<Serializable> domainsToPrimaryKeys(Iterable<T> domains) {
        final Collection<Serializable> keys = new ArrayList<Serializable>();
        Serializable pk;
        for (T d : domains) {
            pk = (Serializable) getPrimaryKey(d);
            keys.add(pk);
        }
        return keys;
    }

    public Collection<ID> domainsToSimpleKeys(Iterable<T> domains) {
        final Collection<ID> keys = new ArrayList<ID>();
        ID simpleKey;
        for (T d : domains) {
            simpleKey = getSimpleKey(d);
            keys.add(simpleKey);
        }
        return keys;
    }
    
    public CursorPage<ID> domainPageToSimplePage(CursorPage<T> domainPage) {
        final CursorPage<ID> idPage = new CursorPage<ID>();
        idPage.setCursorKey(domainPage.getCursorKey());
        idPage.setItems(domainsToSimpleKeys(domainPage.getItems()));
        idPage.setRequestedPageSize(domainPage.getRequestedPageSize());
        return idPage;
    }
    
    /**
     * For test purposes.
     * This implementation does nothing.
     * Override to implement for persistence type.
     */
    public void dropTable() {
    }

    public Collection<ID> coresToSimpleKeys(Iterable<E> cores) {
        final Collection<ID> ids = new ArrayList<ID>();
        ID id;
        for (E core : cores) {
            id = coreToSimpleKey(core);
            ids.add(id);
        }
        return ids;
    }

    public Collection<ID> coreKeysToSimpleKeys(Iterable<C> cores) {
        final Collection<ID> ids = new ArrayList<ID>();
        ID id;
        for (C core : cores) {
            id = coreKeyToSimpleKey(core);
            ids.add(id);
        }
        return ids;
    }

    @Override
    public ID getSimpleKeyByPrimaryKey(Object primaryKey) {
        return coreKeyToSimpleKey((C) primaryKey);
    }
    
    protected final String createMemCacheKeyAll() {
        return String.format("%s.all()", getTableName());
    }
    
    protected final Collection<String> createMemCacheKeys(Object parentKey, Iterable<ID> simpleKeys) {
        Collection<String> returnValue = new ArrayList<String>();
        
        for (ID id : simpleKeys) {
            returnValue.add(createMemCacheKey(parentKey, id));
        }
        
        return returnValue;
    }

    /** Override in GeneratedDaoImpl */
    protected Object getDomainProperty(T domain, String name) {
        Object value;
        if (name.equals(getCreatedByColumnName())) {
            value = getCreatedBy(domain);
        }
        else if (name.equals(getCreatedDateColumnName())) {
            value = getCreatedDate(domain);
        }
        else if (name.equals(getUpdatedByColumnName())) {
            value = getUpdatedBy(domain);
        }
        else if (name.equals(getUpdatedDateColumnName())) {
            value = getUpdatedDate(domain);
        }
        else {
            throw new IllegalArgumentException(String.format("No such property %s for %s", name, getTableName()));
        }
        
        return value;
    }
    
    @Override
    public Object getParentKey(Map<String, String> properties) {
        String value = properties.get(getParentKeyColumnName());
        Class clazz = getColumnClass(getParentKeyColumnName());
        return parseProperty(value, clazz);
    }
    
    @Override
    public ID getSimpleKey(Map<String, String> properties) {
        String value = properties.get(getPrimaryKeyColumnName());
        Class clazz = getColumnClass(getPrimaryKeyColumnName());
        return (ID) parseProperty(value, clazz);
    }

    @Override
    public Collection<ID> getSimpleKeys(Iterable<T> domains) {
        final ArrayList<ID> keys = new ArrayList<ID>();
        for (T t : domains) {
            keys.add(getSimpleKey(t));
        }
        return keys;
    }
    
    protected Cache getMemCache() {
        if (null == memCache) {
            try {
                final CacheFactory factory = CacheManager.getInstance().getCacheFactory();
                memCache = factory.createCache(memCacheConfig);
            }
            catch (CacheException ce) {
                error("Could not create memCache", ce);
            }
        }
        return memCache;
    }
    
    protected final void updateMemCache(Collection<String> memCacheKeys) {
        debug("removing cache for {} {}", memCacheKeys.size(), getTableName());
        if (!memCacheKeys.isEmpty()) {
            // invalidate cache
            if (memCacheAll) {
                getMemCache().remove(createMemCacheKeyAll());
            }

            if (memCacheEntities) {
                for (String memCacheKey : memCacheKeys) {
                    getMemCache().remove(memCacheKey);
                }
            }
        }
    }
    
    protected final void updateMemCache(boolean remove, Map<String, T> domains) {
        if (remove) {
            updateMemCache(domains.keySet());
        }
        else {
            debug("updating cache for %d %s", domains.size(), getTableName());
            if (!domains.isEmpty()) {
                // invalidate cache
                if (memCacheAll) {
                    getMemCache().remove(createMemCacheKeyAll());
                }

                if (memCacheEntities) {
                    getMemCache().putAll(domains);
                }
            }
        }
    }
    
    protected final Collection<T> updateMemCache(boolean remove, Iterable<T> domains) {
        if (memCacheEntities || memCacheAll) {
            Object parentKey;
            String memCacheKey;
            ID simpleKey;
            
            // the returnValue is only to populate memCacheAll
            final Collection<T> returnValue = memCacheAll ? new ArrayList<T>() : null;
            
            final Map<String, T> toCache = new TreeMap<String, T>();
            for (T domain : domains) {
                simpleKey = getSimpleKey(domain);
                parentKey = getParentKey(domain);
                memCacheKey = createMemCacheKey(parentKey, simpleKey);
                toCache.put(memCacheKey, domain);
                if (memCacheAll) {
                    returnValue.add(domain);
                }
            }
            updateMemCache(remove, toCache);
            return returnValue;
        }
        return null;
    }

    protected final Collection<T> updateMemCacheAll(Iterable<T> domains) {
        final Collection<T> returnValue = updateMemCache(false, domains);
        if (memCacheAll && null != returnValue) {
            getMemCache().put(createMemCacheKeyAll(), returnValue);
        }
        return returnValue;
    }

    /** Default implementation returns null, override for your hierarchy */
    public String getParentKeyColumnName() {
        return null;
    }
    
    public static String getPrincipalName() {
        return principalName.get();
    }

    /** Default implementation is void, override for your parent field */
    public void setParentKey(T domain, Object parentKey) {
    }
    
    /** Default implementation returns null, override for your creator field */
    public String getCreatedBy(T domain) {
        return null;
    }

    /** Default implementation returns null, override for your creator field */
    public String getCreatedByColumnName() {
        return null;
    }

    /** Default implementation is void, override for your creator field */
    public void _setCreatedBy(T domain, String creator) {
    }
    
    /** Default implementation returns null, override for your updator field */
    public String getUpdatedBy(T domain) {
        return null;
    }

    /** Default implementation returns null, override for your updator field */
    public String getUpdatedByColumnName() {
        return null;
    }

    /** Default implementation is void, override for your updator field */
    public void _setUpdatedBy(T domain, String updator) {
    }
    
    /** Default implementation returns null, override for your created field */
    public Date getCreatedDate(T domain) {
        return null;
    }

    /** Default implementation returns null, override for your created field */
    public String getCreatedDateColumnName() {
        return null;
    }

    /** Default implementation is void, override for your creator field */
    public void _setCreatedDate(T domain, Date date) {
    }
    
    /** Default implementation returns null, override for your updated field */
    public Date getUpdatedDate(T domain) {
        return null;
    }

    /** Default implementation returns null, override for your updated field */
    public String getUpdatedDateColumnName() {
        return null;
    }

    /** Default implementation is void, override for your updated field */
    public void _setUpdatedDate(T domain, Date date) {
    }
    
    /**
     * Override to return your desired column name
     * @return COLUMN_NAME_GEOBOXES_DEFAULT, i.e. "geoboxes"
     */
    protected String getGeoboxesColumnName() {
        return COLUMN_NAME_GEOBOXES_DEFAULT;
    }
    
    /** Override in GeneratedEntityDaoImpl */
    public String getGeoLocationColumnName() {
        return null;
    }

    public DLocation getGeoLocation(T domain) {
        return null;
    }
    
    /** geoboxes are needed to findGeo the nearest entities before sorting them by distance */
    protected void updateGeoModel(T domain, E core) throws IllegalArgumentException {
        final DLocation location = getGeoLocation(domain);
        final Collection<Long> geoboxes = new ArrayList<Long>();

        // if entity has no location, simply set the domain field to empty collection
        if (null != location) {
            for (int bits : boxBits) {
                geoboxes.addAll(Geobox.getTuple(location.getLatitude(), location.getLongitude(), bits));
            }
            setCoreProperty(core, getGeoboxesColumnName(), geoboxes);
        }
    }

    // --- BEGIN Dao methods ---
    
    public int count() {
        return count(null, null);
    }
    
    public int delete(Object parentKey, Iterable<ID> simpleKeys) {
        final int count = doDelete(parentKey, simpleKeys);
        
        // invalidate cache
        final Collection<String> memCacheKeys = createMemCacheKeys(parentKey, simpleKeys);
        updateMemCache(memCacheKeys);
        
        return count;
    }

    public int delete(Iterable<T> domains) {
        final int count = doDelete(domains);
        
        // invalidate cache (remove)
        updateMemCache(true, domains);
        
        return count;
    }

    public boolean delete(Object parentKey, ID simpleKey) {
        final int count = delete(parentKey, Arrays.asList(simpleKey));
        return 1 == count;
    }
    
    public boolean delete(ID simpleKey) {
        final int count = delete(null, Arrays.asList(simpleKey));
        return 1 == count;
    }
    
    public boolean delete(T domain) {
        final int count = delete(getParentKey(domain), Arrays.asList(getSimpleKey(domain)));
        return 1 == count;
    }
    
    public T findByPrimaryKey(Object parentKey, ID simpleKey) {
        final T cached = getCachedByPrimaryKey(parentKey, simpleKey);
        if (null != cached) {
            return cached;
        }
        
        final T domain = doFindByPrimaryKey(parentKey, simpleKey);
        putCachedByPrimaryKey(parentKey, simpleKey, domain);
        return domain;
    }
    
    public T findByPrimaryKey(ID simpleKey) {
        return findByPrimaryKey(null, simpleKey);
    }

    public T findByPrimaryKey(Object primaryKey) {
        final P parentKey = coreKeyToParentKey((C) primaryKey);
        final ID simpleKey = coreKeyToSimpleKey((C) primaryKey);
        return findByPrimaryKey(parentKey, simpleKey);
    }
    
    public Future<?> findByPrimaryKeyForFuture(Object parentKey, ID simpleKey) {
        final T cached = getCachedByPrimaryKey(parentKey, simpleKey);
        if (null != cached) {
            final FutureTask task = new FutureTask(RUNNABLE_VOID, cached);
            task.run();
            return task;
        }
        
        final Future<?> future = doFindByPrimaryKeyForFuture(parentKey, simpleKey);
        
        // cache will be populated in getDomain(Future);
        
        return future;
    }
    
    public Future<?> findByPrimaryKeyForFuture(ID simpleKey) {
        return findByPrimaryKeyForFuture(null, simpleKey);
    }

    public Future<?> findByPrimaryKeyForFuture(Object primaryKey) {
        final P parentKey = coreKeyToParentKey((C) primaryKey);
        final ID simpleKey = coreKeyToSimpleKey((C) primaryKey);
        return findByPrimaryKeyForFuture(parentKey, simpleKey);
    }
    
    protected T getCachedByPrimaryKey(Object parentKey, ID simpleKey) {
        if (memCacheEntities && null != simpleKey) {
            final String memCacheKey = createMemCacheKey(parentKey, simpleKey);
            T cached = (T) getMemCache().get(memCacheKey);
            if (null != cached) {
                return cached;
            }
        }
        return null;
    }

    protected String putCachedByPrimaryKey(Object parentKey, ID simpleKey, T domain) {
        if (memCacheEntities && null != simpleKey && null != domain) {
            final String memCacheKey = createMemCacheKey(parentKey, simpleKey);
            getMemCache().put(memCacheKey, domain);
            return memCacheKey;
        }
        return null;
    }
    
    @Override
    public T getDomain(Future<?> future) {
        if (null != future) {
            try {
                final Object result = future.get();
                if (null == result) {
                    return null;
                }
                
                // if it was found in cache, it will be the Domain object, not core Entity
                if (this.persistentClass.equals(result.getClass())) {
                    return (T) result;
                }
                
                final T domain = coreToDomain((E) result);
                if (memCacheEntities && null != domain) {
                    final Object parentKey = getParentKey(domain);
                    final ID simpleKey = getSimpleKey(domain);
                    putCachedByPrimaryKey(parentKey, simpleKey, domain);
                }
                return domain;
            } catch (InterruptedException ex) {
                warn("Interrupted", ex);
            } catch (ExecutionException ex) {
                if (null == ex.getCause() ||
                        !"com.google.appengine.api.datastore.EntityNotFoundException"
                        .equals(ex.getCause().getClass().getName())) {
                    warn("Executing", ex);
                }
            }
        }
        return null;
    }

    @Override
    public ID getSimpleKey(Future<?> future) {
        if (null != future) {
            try {
                final Object result = future.get();
                if (null == result) {
                    return null;
                }
                
                // if it was found in cache, it will be the ID object, not core Key
                if (this.simpleIdClass.equals(result.getClass())) {
                    return (ID) result;
                }
                
                final ID simpleKey = coreKeyToSimpleKey((C) result);
                return simpleKey;
            } catch (InterruptedException ex) {
                warn("Interrupted", ex);
            } catch (ExecutionException ex) {
                if (null == ex.getCause() ||
                        !"com.google.appengine.api.datastore.EntityNotFoundException"
                        .equals(ex.getCause().getClass().getName())) {
                    warn("Executing", ex);
                }
            }
        }
        return null;
    }

    @Override
    public Collection<ID> getSimpleKeys(Future<List<?>> future) {
        if (null != future) {
            try {
                final Object result = future.get();
                if (null == result) {
                    return null;
                }
                
                List list = (List) result;
                // return empty list
                if (list.isEmpty()) {
                    return list;
                }
                
                // if it was found in cache, it will be the ID object, not core Key
                Object first = list.get(0);
                if (this.simpleIdClass.equals(first.getClass())) {
                    return (Collection<ID>) list;
                }
                
                final Collection<ID> simpleKeys = coreKeysToSimpleKeys(list);
                
                // populate keys on domains
                if (future instanceof MardaoListFuture) {
                    Iterable<T> domains = ((MardaoListFuture) future).getDomains();
                    Iterator<ID> i = simpleKeys.iterator();
                    for (T d : domains) {
                        setSimpleKey(d, i.next());
                    }
                }
                
                return simpleKeys;
            } catch (InterruptedException ex) {
                warn("Interrupted", ex);
            } catch (ExecutionException ex) {
                if (null == ex.getCause() ||
                        !"com.google.appengine.api.datastore.EntityNotFoundException"
                        .equals(ex.getCause().getClass().getName())) {
                    warn("Executing", ex);
                }
            }
        }
        return null;
    }

    public Object getPrimaryKey(Object parentKey, ID simpleKey) {
        return createCoreKey(parentKey, simpleKey);
    }
    
    public Collection<ID> persist(Iterable<T> domains) {
        final Date currentDate = new Date();
        
        // convert to Core Entity:
        final Collection<E> itrbl = new ArrayList<E>();
        E core;
        for (T d : domains) {
            core = domainToCore(d, currentDate);
            itrbl.add(core);
        }
        
        // batch-persist:
        final Collection<C> keys = persistCore(itrbl);
        
        // collect IDs to return:
        final Collection<ID> ids = new ArrayList<ID>(itrbl.size());
        Iterator<T> ds = domains.iterator();
        T d;
        ID simpleKey;
        for (C c : keys) {
            simpleKey = coreKeyToSimpleKey(c);
            ids.add(simpleKey);
            
            // update domain with generated key?
            d = ds.next();
            if (null == getSimpleKey(d)) {
                setSimpleKey(d, simpleKey);
            }
        }
        
        // update cache (do not remove)
        updateMemCache(false, domains);
        
        return ids;
    }

    public ID persist(T domain) {
        final Iterable<ID> ids = persist(Arrays.asList(domain));
        final ID id = ids.iterator().hasNext() ? ids.iterator().next() : null;
        return id;
    }
    
    @Override
    public Future<?> persistForFuture(T domain) {
        final Date currentDate = new Date();
        debug("persistForFuture %ss", getTableName());
        return doPersistCoreForFuture(domainToCore(domain, currentDate));
    }
    
    @Override
    public Future<List<?>> persistForFuture(Iterable<T> domains) {
        final Date currentDate = new Date();
        debug("persistForFuture %ss", getTableName());
        ArrayList<E> entities = new ArrayList<E>();
        for (T d : domains) {
            entities.add(domainToCore(d, currentDate));
        }
        Future coreFuture = doPersistCoreForFuture(entities);
        final MardaoListFuture returnValue = new MardaoListFuture(coreFuture, domains);
        return returnValue;
    }
    
    public Iterable<T> queryAll() {
        Iterable<T> returnValue = null;

        // try cache first
        if (memCacheAll) {
           returnValue = (Collection<T>) getMemCache().get(createMemCacheKeyAll());
        }
        
        // if no cache or missing, query
        if (null == returnValue) {
            returnValue = queryIterable(false, 0, -1, null, null, null, false, null, false);
            
            // populate memCache, and get the Collection
            if (memCacheAll) {
                returnValue = updateMemCacheAll(returnValue);
                debug("Queried %d entities for %s.queryAll()", ((Collection)returnValue).size(), getTableName());
            }
            else {
                debug("Queried entities for %s.queryAll()", getTableName());
            }
        }
        else {
            debug("Fetched %d entities from memCache %s.queryAll()", ((Collection)returnValue).size(), getTableName());
        }
        return returnValue;
    }
    
    public Iterable<T> queryAll(Object parentKey) {
        Iterable<T> returnValue = null;
        
        // try cache first
        if (memCacheAll) {
            final Collection<T> ts = (Collection<T>) getMemCache().get(createMemCacheKeyAll());
            if (null != ts) {
                
                // filter mem cache by parent key
                final ArrayList<T> domains = new ArrayList<T>();
                returnValue = domains;
                for (T t : ts) {
                    if (null == parentKey || parentKey.equals(getParentKey(t))) {
                        domains.add(t);
                    }
                }
            }
        }

        if (null == returnValue) {
            returnValue = queryIterable(false, 0, -1, (P) parentKey, null, null, false, null, false);
        }
        
        return returnValue;
    }
    
    @Override
    public Iterable<T> queryChunk(int offset, int limit, 
            Object ancestorKey, Object primaryKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
        Iterable<T> returnValue = queryIterable(false, offset, limit, 
                ancestorKey, primaryKey, 
                primaryOrderBy, primaryIsAscending, 
                secondaryOrderBy, secondaryIsAscending, filters);
        return returnValue;
    }
    
    public Iterable<ID> queryAllKeys() {
        Iterable<ID> returnValue = null;
        
        // try cache first
        if (memCacheAll) {
            final Collection<T> ts = (Collection<T>) getMemCache().get(createMemCacheKeyAll());
            if (null != ts) {
                returnValue = domainsToSimpleKeys(ts);
            }
        }

        if (null == returnValue) {
            returnValue = queryIterableKeys(0, -1, null, null, null, false, null, false);
        }
        
        return returnValue;
    }
    
    public Iterable<ID> queryAllKeys(Object parentKey) {
        Iterable<ID> returnValue = null;
        
        // try cache first
        if (memCacheAll) {
            final Collection<T> ts = (Collection<T>) getMemCache().get(createMemCacheKeyAll());
            if (null != ts) {
                
                // filter mem cache by parent key
                final ArrayList<ID> keys = new ArrayList<ID>();
                returnValue = keys;
                ID simpleKey;
                for (T t : ts) {
                    if (null == parentKey || parentKey.equals(getParentKey(t))) {
                        simpleKey = getSimpleKey(t);
                        keys.add(simpleKey);
                    }
                }
            }
        }

        if (null == returnValue) {
            returnValue = queryIterableKeys(0, -1, (P) parentKey, null, null, false, null, false);
        }
        
        return returnValue;
    }
    
    /**
     * Retrieves the full ancestor tree for specified key.
     * @param ancestorKey
     * @return a map with Core Key as key, Domain object as value.
     */
    @Override
    public TreeMap<C, Object> queryByAncestorKey(Object ancestorKey) {
        final Iterable<E> entities = doQueryByAncestorKey((C) ancestorKey);
        final TreeMap<C, Object> domainMap = new TreeMap<C, Object>();
        String kind;
        DaoImpl dao;
        
        for (E core : entities) {
            kind = getCoreKind(core);
            if (null != kind) {
                dao = DAO_MAP.get(kind);
                if (null != dao) {
                    final Object domain = dao.coreToDomain(core);
                    final Object key = dao.getPrimaryKey(domain);
                    domainMap.put((C) key, domain);
                }
            }
        }
        
        return domainMap;
    }
    
    public Iterable<T> queryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys) {
        int entitiesCached = 0, entitiesQueried = 0;
        final Map<ID, T> entities = new TreeMap<ID, T>();
        final TreeSet<ID> missing = new TreeSet<ID>();
        for (ID id : simpleKeys) {
            missing.add(id);
        }
        Collection<String> memCacheKeys = null;
        
        // find in cache
        if (memCacheEntities) {
            
            memCacheKeys = createMemCacheKeys(parentKey, missing);
            try {
                final Map<String,T> cached = getMemCache().getAll(memCacheKeys);
                
                // found entities should not be queried
                ID simpleKey;
                T domain;
                for (Entry<String,T> cacheHit : cached.entrySet()) {
                    domain = cacheHit.getValue();
                    simpleKey = getSimpleKey(domain);
                    missing.remove(simpleKey);
                    
                    // add to found entities
                    entities.put(simpleKey, cacheHit.getValue());
                }
                entitiesCached = entities.size();
                
            } catch (CacheException ex) {
                warn(String.format("Error getting cached %ss", getTableName()));
            }
            catch (NullPointerException ifNoCache) {
                memCacheEntities = false;
                warn("Disabling non-functional cache for {}.memCacheEntities", getTableName());
            }
        }
         
        // cache miss?
        if (!missing.isEmpty()) {
            final Iterable<T> queried = doQueryByPrimaryKeys(parentKey, missing);
            final Map<String, T> toCache = new HashMap<String, T>(missing.size());
            
            ID simpleKey;
            String memCacheKey;
            for (T domain : queried) {
                
                // add to returnValue
                simpleKey = getSimpleKey(domain);
                entities.put(simpleKey, domain);
                
                // add to toCache map
                memCacheKey = createMemCacheKey(parentKey, simpleKey);
                toCache.put(memCacheKey, domain);
            }

            // update cache (batch style)
            updateMemCache(false, toCache);
            entitiesQueried = entities.size();
        }
        
        debug("cached:%d, queried:%d", entitiesCached, entitiesQueried);
        return entities.values();
    }
    
    public CursorPage<T> queryPage(int pageSize, String cursorString) {
        return queryPage(false, pageSize, null, null,
                null, false, null, false,
                cursorString);
    }

    public CursorPage<T> queryPage(int pageSize, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            String cursorString) {
        return queryPage(false, pageSize, null, null,
                primaryOrderBy, primaryIsAscending, secondaryOrderBy, secondaryIsAscending,
                cursorString);
    }

    public CursorPage<T> queryInGeobox(float lat, float lng, int bits, int pageSize, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            String cursorString, Filter... filters) {
        if (!boxBits.contains(bits)) {
            throw new IllegalArgumentException("Unboxed resolution, hashed are " + boxBits);
        }
        
        final long box = Geobox.getHash(lat, lng, bits);
        
        final Filter geoFilters[] = Arrays.copyOf(filters, filters != null ? filters.length + 1 : 1, Filter[].class);
        geoFilters[geoFilters.length-1] = createEqualsFilter(getGeoboxesColumnName(), box);
        return queryPage(false, pageSize, null, null, primaryOrderBy, primaryIsAscending, secondaryOrderBy, secondaryIsAscending, 
                cursorString, geoFilters);
    }

    public Collection<T> findNearest(final float lat, final float lng, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            int offset, int limit, Filter... filters) {
        final DLocation p = new DLocation(lat, lng);
        final int size = offset + (0 < limit ? limit : 10000);
        
        // sorting on distance has to be done outside datastore, i.e. here in application:
        Map<Double, T> orderedMap = new TreeMap<Double, T>();
        for (int bits : boxBits) {       
            final CursorPage<T> subList = queryInGeobox(lat, lng, bits, size,
                    primaryOrderBy, primaryIsAscending, secondaryOrderBy, secondaryIsAscending,
                    null, filters);
            final double S = Geobox.getCellSize(bits, lat);
            for (T model : subList.getItems()) {
                double d = Geobox.distance(getGeoLocation(model), p);
                if (d < S) {
                    orderedMap.put(d, model);
                }
            }
            
            if (size <= orderedMap.size()) {
                break;
            }
        }
        // return with specified offset and limit
        final Collection<T> values = orderedMap.values();
        T[] page = (T[]) Arrays.copyOfRange(values.toArray(), 
                Math.min(offset, values.size()), Math.min(size, values.size()));
        return Arrays.asList(page);
    }

    /**
     * Implemented with a call to persist(domains). Feel free to override.
     * @param domains 
     */
    public void update(Iterable<T> domains) {
        // for this implementation, same as persist
        persist(domains);
    }

    /**
     * Implemented with a call to persist(domains). Feel free to override.
     * @param domain
     */
    public void update(T domain) {
        persist(Arrays.asList(domain));
    }
    
    public static String escapeCsv(Object s) {
        if (null == s) {
            return null;
        }
        // first, escape all double quotes:
        final String escaped = s.toString().replaceAll("\\\"", "\"\"");
        // then, qoute the string:
        final String quoted = String.format("\"%s\"", escaped);
        
        return quoted;
    }

    public Map<String, Object> getCsvColumnValues(DaoImpl dao, String[] columns, T domain) {
        final HashMap<String, Object> values = new HashMap<String, Object>();
        for (String col : columns) {
            values.put(col, getDomainProperty(domain, col));
        }
        return values;
    }
    
    public void writeAsCsv(OutputStream out, String[] columns, CsvConverter<T> converter,
            Object ancestorKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending, 
            Filter... filters) {
        final Iterable<T> qi = queryIterable(false, 0, -1, ancestorKey, null, 
                            primaryOrderBy, primaryIsAscending, secondaryOrderBy, secondaryIsAscending, 
                            filters);
        writeAsCsv(out, columns, converter, qi);
    }

    public void writeAsCsv(OutputStream out, String[] columns, Iterable<T> qi) {
        writeAsCsv(out, columns, this, qi);
    }
    
    public void writeAsCsv(OutputStream out, String[] columns, CsvConverter<T> converter, Iterable<T> qi) {
        
        final PrintWriter pw = new PrintWriter(out);
        final StringBuffer sb = new StringBuffer();
        
        // write header row
        boolean isFirst = true;
        for (String col : columns) {
            // check column is property, primaryKey or parent:
            if (!getColumnNames().contains(col)) {
                if (!getPrimaryKeyColumnName().equals(col)) {
                    if (!col.equals(getParentKeyColumnName())) {
                        warn("No such column %s", col);
                        continue;
                    }
                }
            }
            
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append(',');
            }
            sb.append(escapeCsv(col));
        }
        pw.println(sb.toString());
        debug(sb.toString());

        // now write each row
        for (T domain : qi) {
            sb.setLength(0);
            isFirst = true;
            for (String col : columns) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    sb.append(',');
                }
                sb.append(escapeCsv(getDomainProperty(domain, col)));
            }

            pw.println(sb.toString());
            debug(sb.toString());
        }
        pw.flush();
    }
    
    /**
     * Returns the IDs for the entities with updatedDate >= since, in descending order.
     * @param since
     * @return the IDs for the entities with updatedDate >= since, in descending order.
     */
    @Override
    public CursorPage<ID> whatsChanged(Date since, int pageSize, String cursorKey) {
        return whatsChanged(null, since, pageSize, cursorKey);
    }
    
    /**
     * Returns the IDs for the entities with updatedDate >= since, in descending order.
     * @param since
     * @return the IDs for the entities with updatedDate >= since, in descending order.
     */
    @Override
    public CursorPage<ID> whatsChanged(Object parentKey, Date since, 
            int pageSize, String cursorKey, Filter... filters) {
        final String updatedDateColumnName = getUpdatedDateColumnName();
        if (null == updatedDateColumnName) {
            throw new UnsupportedOperationException("Not supported without @UpdatedDate");
        }
        CursorPage<ID> idPage = null;
        String auditCursorKey = cursorKey;
        
        // start with returning updated IDs
        if (null == cursorKey || !cursorKey.startsWith(AUDIT_CURSOR_PREFIX)) {
            auditCursorKey = null;
            Filter allFilters[] = Arrays.copyOf(filters, (null != filters ? filters.length : 0) + 1);
            allFilters[allFilters.length-1] = createGreaterThanOrEqualFilter(updatedDateColumnName, since);
            final CursorPage<T> entityPage = queryPage(true, pageSize, parentKey, null, 
                                      null, false, null, false, 
//                                      updatedDateColumnName, true, null, false, 
                                      cursorKey, allFilters);
        
            // convert entities to IDs only
            idPage = domainPageToSimplePage(entityPage);
        }
        
        // add all deleted IDs to last page
        if (null == idPage || // audit cursor key
                null == idPage.getCursorKey() || // no more udated items
                idPage.getItems().size() < pageSize) { // incomplete updated page
        
            // full audit page or append to existing?
            int remainingSize = null == idPage ? pageSize : 
                    pageSize - idPage.getItems().size();
            final CursorPage<ID> deletedKeys = whatsDeleted(since, 
                    remainingSize, auditCursorKey);
            if (null == idPage) {
                idPage = deletedKeys;
            }
            else {
                idPage.getItems().addAll(deletedKeys.getItems());
                idPage.setCursorKey(deletedKeys.getCursorKey());
            }
        }
        return idPage;
    }

    // --- END Dao methods ---
    
    /** Override in GeneratedDaoImpl */
    protected void setDomainProperty(final T domain, final String name, final Object value) {
        if (name.equals(getCreatedByColumnName())) {
            _setCreatedBy(domain, (String) value);
        }
        else if (name.equals(getCreatedDateColumnName())) {
            _setCreatedDate(domain, (Date) value);
        }
        else if (name.equals(getUpdatedByColumnName())) {
            _setUpdatedBy(domain, (String) value);
        }
        else if (name.equals(getUpdatedDateColumnName())) {
            _setUpdatedDate(domain, (Date) value);
        }
        else {
            throw new IllegalArgumentException(String.format("No such property %s for %s", name, getTableName()));
        }
    }
    
    protected void setMapProperty(Map<String, Object> map, final String name, final Object value) {
        if (null != map && null != name) {
            map.put(name, value);
        }
    }
    
    public static Object parseProperty(String value, Class clazz) {
        if (null == value) {
            return null;
        }
        
        if (Long.class.equals(clazz)) {
            return Long.parseLong(value);
        }
        if (Integer.class.equals(clazz)) {
            return Integer.parseInt(value);
        }
        if (Short.class.equals(clazz)) {
            return Short.parseShort(value);
        }
        if (Byte.class.equals(clazz)) {
            return Byte.parseByte(value);
        }
        if (String.class.equals(clazz)) {
            return value;
        }
        if (Date.class.equals(clazz)) {
            long l = Long.parseLong(value);
            return new Date(l);
        }
        throw new UnsupportedOperationException("Unparseable property " + value + " of class " + clazz.getName());
    }

    public void setMardaoParentDao(DaoImpl mardaoParentDao) {
        this.mardaoParentDao = mardaoParentDao;
    }
    
    public void setBoxBits(Collection<Integer> boxBits) {
        this.boxBits = boxBits;
    }

    public static void setPrincipalName(String name) {
        principalName.set(name);
    }

    public static void setMemCacheConfig(Map memCacheConfig) {
        DaoImpl.memCacheConfig = memCacheConfig;
    }

}
