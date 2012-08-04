package net.sf.mardao.api.dao;

import com.google.appengine.api.datastore.DatastoreFailureException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.sf.mardao.api.domain.AEDPrimaryKeyEntity;
import net.sf.mardao.api.domain.CreatedUpdatedEntity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AEDDaoImpl<T extends AEDPrimaryKeyEntity<ID>, ID extends Serializable> extends
        DaoImpl<T, ID, Key, Entity, Key> implements Dao<T, ID, Key, Key> {
    
    /** Using slf4j logging */
    protected final Logger   LOG = LoggerFactory.getLogger(getClass());
    
    /** Will be populated by the children in afterPropertiesSet */
    protected final Collection<AEDDaoImpl> childDaos = new ArrayList<AEDDaoImpl>();

    /** Will be populated by the all children in afterPropertiesSet */
    private static final Collection<AEDDaoImpl> applicationDaos = new ArrayList<AEDDaoImpl>();

    private static AEDDaoImpl mardaoParentDao;
    
    protected AEDDaoImpl(Class<T> type) {
        super(type);
    }

    /** Registers at applicationDaos and optionally at parentDao */
    public void init() {
        AEDDaoImpl.getApplicationDaos().add(this);
        if (null != mardaoParentDao) {
            mardaoParentDao.registerChildDao(this);
        }
    }
    
    protected final void registerChildDao(AEDDaoImpl childDao) {
        childDaos.add(childDao);
    }

    @SuppressWarnings("rawtypes")
    protected static final void convertCreatedUpdatedDates(Entity from, AEDPrimaryKeyEntity domain) {
        if (null != domain._getNameCreatedDate()) {
            domain._setCreatedDate((Date) from.getProperty(domain._getNameCreatedDate()));
        }

        if (null != domain._getNameUpdatedDate()) {
            domain._setUpdatedDate((Date) from.getProperty(domain._getNameUpdatedDate()));
        }
    }

    protected static final String convertText(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Text) {
            final Text text = (Text) value;
            return text.getValue();
        }
        return (String) value;
    }

    protected Entity createEntity(ID primaryKey) {
        if (null != primaryKey) {
            return new Entity(createKey(primaryKey));
        }
        return new Entity(getTableName());
    }

    protected Entity createEntity(Key parentKey, ID primaryKey) {
        if (null != primaryKey) {
            return new Entity(createKey(parentKey, primaryKey));
        }
        return new Entity(getTableName(), parentKey);
    }

    @Override
    protected final Expression createEqualsFilter(String fieldName, Object param) {
        return new FilterEqual(fieldName, param);
    }

    protected static void populate(Entity entity, String name, Object value) {
        if (null != value) {
            // String properties must be 500 characters or less.
            // Instead, use com.google.appengine.api.datastore.Text, which can store strings of any length.
            if (value instanceof String) {
                final String s = (String) value;
                if (500 < s.length()) {
                    value = new Text(s);
                }
            }
        }
        entity.setProperty(name, value);
    }

    protected static DatastoreService getDatastoreService() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    protected List<T> asIterable(PreparedQuery pq, int limit, int offset) {
        final List<T> returnValue = new ArrayList<T>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (0 < limit) {
            fetchOptions.limit(limit);
        }

        if (0 < offset) {
            fetchOptions.offset(offset);
        }

        T domain;
        for(Entity entity : pq.asIterable(fetchOptions)) {
            domain = createDomain(entity);
            returnValue.add(domain);
            // LOGGER.debug("  entity {} -> domain {}", entity, domain);
        }
        return returnValue;
    }

    protected T asSingleEntity(PreparedQuery pq) {
        final Entity entity = pq.asSingleEntity();
        final T domain = createDomain(entity);
        return domain;
    }

    protected List<ID> asKeys(PreparedQuery pq, int limit, int offset) {
        final List<ID> returnValue = new ArrayList<ID>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (0 < limit) {
            fetchOptions.limit(limit);
        }

        if (0 < offset) {
            fetchOptions.offset(offset);
        }

        ID key;
        for(Entity entity : pq.asIterable(fetchOptions)) {
            key = convert(entity.getKey());
            returnValue.add(key);
        }

        return returnValue;
    }

    protected List<Key> asCoreKeys(PreparedQuery pq, int limit, int offset) {
        final List<Key> returnValue = new ArrayList<Key>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (0 < limit) {
            fetchOptions.limit(limit);
        }

        if (0 < offset) {
            fetchOptions.offset(offset);
        }

        for(Entity entity : pq.asIterable(fetchOptions)) {
            returnValue.add(entity.getKey());
        }

        return returnValue;
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(Map<String, Object> filters, String orderBy, boolean direction) {
        return prepare(filters, orderBy, direction, null, false);
    }

    /**
     * @param direction
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(Map<String, Object> filters, String orderBy, boolean direction, String secondaryOrderBy,
            boolean secondaryDirection) {
        LOG.debug("prepare {} filters {}", getTableName(), filters);
        final DatastoreService datastore = getDatastoreService();

        Query q = new Query(getTableName());

        // filter query:
        if (null != filters) {
            for(Entry<String, Object> filter : filters.entrySet()) {
                q.addFilter(filter.getKey(), FilterOperator.EQUAL, filter.getValue());
            }
        }

        // sort query?
        if (null != orderBy) {
            q.addSort(orderBy, direction ? SortDirection.ASCENDING : SortDirection.DESCENDING);

            // secondary sort order?
            if (null != secondaryOrderBy) {
                q.addSort(secondaryOrderBy, secondaryDirection ? SortDirection.ASCENDING : SortDirection.DESCENDING);
            }
        }

        return datastore.prepare(q);
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(boolean keysOnly, String orderBy, boolean ascending, Expression... filters) {
        return prepare(keysOnly, null, null, orderBy, ascending, filters);
    }

    /**
     * 
     * @param keysOnly
     * @param parentKey
     * @param orderBy
     * @param ascending
     * @param filters
     * @return
     */
    protected PreparedQuery prepare(boolean keysOnly, Key parentKey, Key simpleKey, String orderBy, boolean ascending,
            Expression... filters) {
        LOG.debug("prepare {} with filters {}", getTableName(), filters);
        final DatastoreService datastore = getDatastoreService();

        Query q = new Query(getTableName(), parentKey);

        // keys only?
        if (keysOnly) {
            q.setKeysOnly();
        }

        // filter on keyName:
        if (null != simpleKey) {
            q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, simpleKey);
        }

        // filter query:
        if (null != filters) {
            for(Expression expression : filters) {
                q.addFilter(expression.getColumn(), (FilterOperator) expression.getOperation(), expression.getOperand());
            }
        }

        // sort query?
        if (null != orderBy) {
            q.addSort(orderBy, ascending ? SortDirection.ASCENDING : SortDirection.DESCENDING);
        }

        return datastore.prepare(q);
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(String orderBy, boolean ascending, Expression... filters) {
        return prepare(false, orderBy, ascending, filters);
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepareKeys(String orderBy, boolean ascending, Expression... filters) {
        return prepare(true, orderBy, ascending, filters);
    }

    @Override
    protected final List<T> findByParent(Key parentKey) {
        final PreparedQuery query = prepare(false, parentKey, null, null, false);
        return asIterable(query, -1, 0);
    }

    public final T findByPrimaryKey(Key parentKey, ID primaryKey) {
        T domain = null;
        final Key key = createKey((Key) parentKey, primaryKey);
        final DatastoreService datastore = getDatastoreService();
        try {
            final Entity entity = datastore.get(key);
            domain = createDomain(entity);
        }
        catch (EntityNotFoundException ignore) {
        }
        LOG.debug("{} -> {}", key.toString(), domain);

        return domain;
    }

    public final Map<ID, T> findByPrimaryKeys(Key parentKey, Iterable<ID> primaryKeys) {
        final Map<ID, T> returnValue = new TreeMap<ID, T>();
        final List<Key> keys = new ArrayList<Key>();
        Key key;
        for(ID id : primaryKeys) {
            key = createKey((Key) parentKey, id);
            keys.add(key);
        }
        final DatastoreService datastore = getDatastoreService();
        final Map<Key, Entity> entities = datastore.get(keys);
        T domain;
        ID id;
        for(Entry<Key, Entity> entry : entities.entrySet()) {
            id = convert(entry.getKey());
            domain = createDomain(entry.getValue());
            returnValue.put(id, domain);
        }
        return returnValue;
    }

    public List<ID> findAllKeys() {
        PreparedQuery pq = prepare(true, Entity.KEY_RESERVED_PROPERTY, true);
        return asKeys(pq, -1, 0);
    }

    @Override
    protected final Key persistEntity(Entity entity) {
        final DatastoreService datastore = getDatastoreService();

        return datastore.put(entity);
    }

    public List<ID> persist(Iterable<T> domains) {
        final List<ID> ids = update(domains);
        return ids;
    }

    @Override
    protected final void persistUpdateDates(CreatedUpdatedEntity domain, Entity entity, Date date) {

        // populate createdDate
        String propertyName = domain._getNameCreatedDate();
        if (null != propertyName) {

            // only if not previously created
            if (null == entity.getProperty(propertyName)) {
                entity.setProperty(propertyName, date);
                domain._setCreatedDate(date);
            }
        }

        // update updatedDate
        propertyName = domain._getNameUpdatedDate();
        if (null != propertyName) {

            // always update the date
            entity.setProperty(propertyName, date);
            domain._setUpdatedDate(date);
        }
    }

    protected static final List<Key> persistByCore(Iterable<Entity> entities) {
        final DatastoreService datastore = getDatastoreService();

        try {
            return datastore.put(entities);
        }
        catch (DatastoreFailureException ex) {
            LoggerFactory.getLogger(AEDDaoImpl.class).warn("Re-trying with allocated ids: {}", ex.getMessage());
            // the id allocated for a new entity was already in use, please try again
            ArrayList<Key> keys = new ArrayList<Key>();
            for (Entity entity : entities) {
                KeyRange range = datastore.allocateIds(entity.getParent(), entity.getKind(), 1);
                Entity clone = new Entity(range.getStart());
                clone.setPropertiesFrom(entity);
                try {
                    keys.add(datastore.put(clone));
                }
                catch (DatastoreFailureException inner) {
                    LoggerFactory.getLogger(AEDDaoImpl.class).error("Could not persist Clone with Key" + clone.getKey(), inner);
                    throw inner;
                }
            }
            return keys;
        }
    }

    protected final List<Key> updateByCore(Iterable<Entity> entities) {
        return persistByCore(entities);
    }

    @Override
    public final void deleteByCore(Key primaryKey) {
        // trivial optimization
        final DatastoreService datastore = getDatastoreService();
        datastore.delete(primaryKey);
    }

    public final void deleteByCore(Iterable<Key> primaryKeys) {
        final DatastoreService datastore = getDatastoreService();
        datastore.delete(primaryKeys);
    }

    protected List<T> findByKey(String fieldName, Class<?> foreignClass, Object key) {
        throw new UnsupportedOperationException("Not yet implemented for AED");
    }

    protected T findBy(Map<String, Object> args) {
        PreparedQuery pq = prepare(args, null, false);
        return asSingleEntity(pq);
    }

    protected T findBy(Expression... filters) {
        PreparedQuery pq = prepare(null, false, filters);
        return asSingleEntity(pq);
    }

    protected List<T> findBy(String fieldName, Collection param) {
        final Expression filters[] = new Expression[param.size()];
        
        int i = 0;
        for (Object p : param) {
            filters[i++] = createEqualsFilter(fieldName, p);
        }
        
        LOG.debug("Multiple values for {} should match {}", fieldName, filters);
        
        final PreparedQuery pq = prepare(null, false, filters);
        return asIterable(pq, -1, 0);
    }

    protected List<T> findBy(Map<String, Object> filters, String primaryOrderBy, boolean primaryDirection,
            String secondaryOrderBy, boolean secondaryDirection, int limit, int offset) {
        PreparedQuery pq = prepare(filters, primaryOrderBy, primaryDirection, secondaryOrderBy, secondaryDirection);
        return asIterable(pq, limit, offset);
    }

    protected List<T> findBy(String orderBy, boolean ascending, int limit, int offset, Key parentKey, Expression... filters) {
        PreparedQuery pq = prepare(false, parentKey, null, orderBy, ascending, filters);
        return asIterable(pq, limit, offset);
    }

    protected List<ID> findKeysBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        PreparedQuery pq = prepareKeys(orderBy, ascending, filters);
        return asKeys(pq, limit, offset);
    }

    @Override
    protected List<Key> findCoreKeysByParent(Key parentKey) {
        final PreparedQuery query = prepare(true, parentKey, null, null, false);
        return asCoreKeys(query, -1, 0);
    }

    @Override
    protected List<ID> findKeysByParent(Key parentKey) {
        final PreparedQuery query = prepare(true, parentKey, null, null, false);
        return asKeys(query, -1, 0);
    }

    public List<T> findByManyToMany(String primaryKeyName, String fieldName, String foreignSimpleClass, String foreignFieldName,
            Object foreignId) {
        throw new UnsupportedOperationException("Not yet implemented for AED");
    }

    public final int deleteAll() {
        PreparedQuery pq = prepareKeys(null, false);
        List<Key> keys = asCoreKeys(pq, -1, 0);
        deleteByCore(keys);
        return keys.size();
    }

    public int update(Map<String, Object> values, Expression... expressions) {
        throw new UnsupportedOperationException("Not yet implemented for AED");
    }

    public static AEDDaoImpl getMardaoParentDao() {
        return mardaoParentDao;
    }

    public static void setMardaoParentDao(AEDDaoImpl parentDao) {
        AEDDaoImpl.mardaoParentDao = parentDao;
    }

    public static Collection<AEDDaoImpl> getApplicationDaos() {
        return applicationDaos;
    }

}
