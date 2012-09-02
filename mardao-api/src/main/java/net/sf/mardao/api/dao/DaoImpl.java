package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import net.sf.mardao.api.Filter;
import net.sf.mardao.api.domain.CreatedUpdatedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
public abstract class DaoImpl<T extends CreatedUpdatedEntity<ID>, ID extends Serializable, 
        P extends Serializable, CT extends Object,
        E extends Serializable, C extends Serializable>
        implements Dao<T, ID, P> {
    
    public static final String PRINCIPAL_NAME_ANONYMOUS = "[ANONYMOUS]";

    /** Using slf4j logging */
    protected static final Logger   LOG = LoggerFactory.getLogger(DaoImpl.class);
    
    /** mostly for logging */
    protected final Class<T> persistentClass;
    
    /** set this, to have createdBy and updatedBy set */
    private static final ThreadLocal<String> principalName = new ThreadLocal<String>();

    protected DaoImpl(Class<T> type) {
        this.persistentClass = type;
    }

    public String getTableName() {
        return persistentClass.getSimpleName();
    }

    // --- BEGIN persistence-type beans must implement these ---
    
    public abstract Collection<ID> persist(Iterable<T> domains);
    
    protected abstract CursorPage queryPage(boolean keysOnly, int pageSize,
            C ancestorKey, C simpleKey,
            String primaryOrderBy, boolean primaryIsAscending,
            String secondaryOrderBy, boolean secondaryIsAscending,
            String cursorString,
            Filter... filters);

    /** Implemented in TypeDaoImpl */
    protected abstract ID coreToSimpleKey(E core);
    /** Implemented in TypeDaoImpl */
    protected abstract P coreToParentKey(E core);
    
    
    protected abstract E createCore(Object parentKey, ID simpleKey);
    
    /** Implemented in TypeDaoImpl */
    protected abstract Object getCoreProperty(E core, String name);
    /** Implemented in TypeDaoImpl */
    protected abstract void setCoreProperty(E core, String name, Object value);
    
    // --- END persistence-type beans must implement these ---
    
    public T coreToDomain(E core) {
        final ID simpleKey = coreToSimpleKey(core);
        final P parentKey = coreToParentKey(core);
        
        try {
            final T domain = createDomain(parentKey, simpleKey);

            // created, updated
            copyCorePropertyToDomain(domain._getNameCreatedBy(), core, domain);
            copyCorePropertyToDomain(domain._getNameCreatedDate(), core, domain);
            copyCorePropertyToDomain(domain._getNameUpdatedBy(), core, domain);
            copyCorePropertyToDomain(domain._getNameUpdatedDate(), core, domain);

            // Domain Entity-specific properties
            for (String name : getColumnNames()) {
                copyCorePropertyToDomain(name, core, domain);
            }

            return domain;
        }
        catch (IllegalAccessException shouldNeverHappen) {
            LOG.error(getTableName(), shouldNeverHappen);
        }
        catch (InstantiationException shouldNeverHappen) {
            LOG.error(getTableName(), shouldNeverHappen);
        }
        return null;
    }
    
    protected Object copyCorePropertyToDomain(String name, E core, T domain) {
        if (null == name) {
            return null;
        }
        
        Object value = getCoreProperty(core, name);
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
    
    protected T createDomain() throws InstantiationException, IllegalAccessException {
        return createDomain(null, null);
    }

    protected T createDomain(Object parentKey, ID simpleKey) throws InstantiationException, IllegalAccessException {
        final T domain = persistentClass.newInstance();
        
        domain.setParentKey(parentKey);
        domain.setSimpleKey(simpleKey);
        
        return domain;
    }
    
    public E domainToCore(T domain, final Date currentDate) {
        E core = createCore(domain.getParentKey(), domain.getSimpleKey());
        
        // created, updated
        String principalName = domain.getCreatedBy();
        if (null == principalName) {
            principalName = getPrincipalName();
            if (null == principalName) {
                principalName = PRINCIPAL_NAME_ANONYMOUS;
            }
            domain._setCreatedBy(principalName);
        }
        setCoreProperty(core, domain._getNameCreatedBy(), principalName);
        
        Date date = domain.getCreatedDate();
        if (null == date) {
            date = currentDate;
            domain._setCreatedDate(currentDate);
        }
        setCoreProperty(core, domain._getNameCreatedDate(), date);
        
        principalName = getPrincipalName();
        if (null == principalName) {
            principalName = PRINCIPAL_NAME_ANONYMOUS;
        }
        domain._setUpdatedBy(principalName);
        setCoreProperty(core, domain._getNameUpdatedBy(), principalName);
        
        domain._setUpdatedDate(currentDate);
        setCoreProperty(core, domain._getNameUpdatedDate(), currentDate);

        // Domain Entity-specific properties
        for (String name : getColumnNames()) {
            copyCorePropertyToDomain(name, core, domain);
        }

        return core;
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

    /** Override in GeneratedDaoImpl */
    protected Object getDomainProperty(T domain, String name) {
        Object value;
        if (name.equals(domain._getNameCreatedBy())) {
            value = domain.getCreatedBy();
        }
        else if (name.equals(domain._getNameCreatedDate())) {
            value = domain.getCreatedDate();
        }
        else if (name.equals(domain._getNameUpdatedBy())) {
            value = domain.getUpdatedBy();
        }
        else if (name.equals(domain._getNameUpdatedDate())) {
            value = domain.getUpdatedDate();
        }
        else {
            throw new IllegalArgumentException(String.format("No such property %s for %s", name, getTableName()));
        }
        
        return value;
    }
    
    public static String getPrincipalName() {
        return principalName.get();
    }
    
    public ID persist(T domain) {
        final Iterable<ID> ids = persist(Arrays.asList(domain));
        final ID id = ids.iterator().hasNext() ? ids.iterator().next() : null;
        return id;
    }
    
    public CursorPage<T> queryPage(int pageSize, String cursorString) {
        return queryPage(false, pageSize, null, null,
                null, false, null, false,
                cursorString);
    }
    
    /** Override in GeneratedDaoImpl */
    protected void setDomainProperty(final T domain, final String name, final Object value) {
        if (name.equals(domain._getNameCreatedBy())) {
            domain._setCreatedBy((String) value);
        }
        else if (name.equals(domain._getNameCreatedDate())) {
            domain._setCreatedDate((Date) value);
        }
        else if (name.equals(domain._getNameUpdatedBy())) {
            domain._setUpdatedBy((String) value);
        }
        else if (name.equals(domain._getNameUpdatedDate())) {
            domain._setUpdatedDate((Date) value);
        }
        else {
            throw new IllegalArgumentException(String.format("No such property %s for %s", name, getTableName()));
        }
    }
    
    public static void setPrincipalName(String name) {
        principalName.set(name);
    }
    
//    public final Map<ID, T> findByPrimaryKeys(Key parentKey, Iterable<ID> primaryKeys) {
//        int entitiesCached = 0, entitiesQueried = 0;
//        final Map<ID, T> returnValue = new TreeMap<ID, T>();
//        
//        // convert to Keys
//        final List<Key> keys = new ArrayList<Key>();
//        Key key;
//        for(ID id : primaryKeys) {
//            key = createKey((Key) parentKey, id);
//            keys.add(key);
//        }
//        
//        ID id;
//        // check cache first
//        Map cached = null;
//        if (memCacheEntity) {
//            try {
//                cached = getMemCache().getAll(keys);
//                
//                // found entities should not be queried
//                keys.removeAll(cached.keySet());
//                
//                // add to returnValue
//                Set<Map.Entry<Key, T>> cachedEntries = cached.entrySet();
//                for (Map.Entry<Key, T> entry : cachedEntries) {
//                    id = convert(entry.getKey());
//                    returnValue.put(id, entry.getValue());
//                }
//                entitiesCached = cached.size();
//            } catch (CacheException ignore) {
//            }
//        }
//        
//        // cache miss?
//        if (!keys.isEmpty()) {
//            final DatastoreService datastore = getDatastoreService();
//            final Map<Key, Entity> entities = datastore.get(keys);
//            T domain;
//            final Map<Key, T> toCache = new HashMap<Key, T>(entities.size());
//            for(Map.Entry<Key, Entity> entry : entities.entrySet()) {
//                id = convert(entry.getKey());
//                domain = createDomain(entry.getValue());
//                returnValue.put(id, domain);
//                toCache.put(entry.getKey(), domain);
//            }
//
//            if (memCacheEntity) {
//                getMemCache().putAll(toCache);
//            }
//            entitiesQueried = entities.size();
//        }
//        
//        LOG.debug("cached:{}, queried:{}", entitiesCached, entitiesQueried);
//        return returnValue;
//    }
//

}
