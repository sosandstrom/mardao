package net.sf.mardao.api.dao;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreFailureException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import net.sf.mardao.api.domain.AEDPrimaryKeyEntity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.apphosting.api.ApiProxy.ApiDeadlineExceededException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class AEDDaoImpl<T extends AEDPrimaryKeyEntity<ID>, ID extends Serializable> extends
        DaoImpl<T, ID, Key, Entity, Key> implements Dao<T, ID, Key, Key> {
    
    /** Will be populated by the children in afterPropertiesSet */
    protected final Collection<AEDDaoImpl> childDaos = new ArrayList<AEDDaoImpl>();

    /** Will be populated by the all children in afterPropertiesSet */
    private static final Collection<AEDDaoImpl> applicationDaos = new ArrayList<AEDDaoImpl>();
    
    /** Set this to true in subclass (TypeDaoBean) to enable the MemCache primaryKey - Entity */
    protected boolean memCacheEntity = false;

    /** Set this to true in subclass (TypeDaoBean) to enable the MemCache for findAll */
    protected boolean memCacheAll = false;
    
    protected Cache memCache = null;

    private AEDDaoImpl mardaoParentDao;
    
    protected AEDDaoImpl(Class<T> type) {
        super(type);
    }

    /** Registers at applicationDaos and optionally at parentDao */
    public void init() {
        AEDDaoImpl.getApplicationDaos().add(this);
        if (null != mardaoParentDao) {
            mardaoParentDao.registerChildDao(this);
        }
        
        // initialize MemCache?
        if (memCacheAll || memCacheEntity) {
            LOG.debug("initializing memCache for {}.", getTableName());
            try {
                CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
                memCache = cacheFactory.createCache(Collections.emptyMap());
            } catch (CacheException e) {
                LOG.error("Cannot initialize MemCache", e);
                memCacheAll = false;
                memCacheEntity = false;
            }            
        }
    }
    
    protected final void registerChildDao(AEDDaoImpl childDao) {
        childDaos.add(childDao);
    }

    /** This method is called from (generated) createDomain(E entity) */
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

    /**
     * This method is called from (generated) createEntity(T domain)
     * @param entity
     * @param name
     * @param value 
     */
    @Override
    protected final void populate(Entity entity, String name, Object value) {
        if (null != name && null != value) {
            // String properties must be 500 characters or less.
            // Instead, use com.google.appengine.api.datastore.Text, which can store strings of any length.
            if (value instanceof String) {
                final String s = (String) value;
                if (500 < s.length()) {
                    value = new Text(s);
                }
            }
            entity.setProperty(name, value);
        }
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

    protected QueryResultIterable asQueryResultIterable(PreparedQuery pq) {
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        fetchOptions.chunkSize(100);

        return pq.asQueryResultIterable(fetchOptions);
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
        
        // check cache first
        if (memCacheEntity) {
            domain = (T) memCache.get(key);
        }
        
        if (null == domain) {
            try {
                final DatastoreService datastore = getDatastoreService();
                final Entity entity = datastore.get(key);
                domain = createDomain(entity);
                
                if (memCacheEntity && null != domain) {
                    memCache.put(key, domain);
                }
            }
            catch (EntityNotFoundException ignore) {
            }
        }
        LOG.debug("{} -> {}", key.toString(), domain);

        return domain;
    }

    public final Map<ID, T> findByPrimaryKeys(Key parentKey, Iterable<ID> primaryKeys) {
        int entitiesCached = 0, entitiesQueried = 0;
        final Map<ID, T> returnValue = new TreeMap<ID, T>();
        
        // convert to Keys
        final List<Key> keys = new ArrayList<Key>();
        Key key;
        for(ID id : primaryKeys) {
            key = createKey((Key) parentKey, id);
            keys.add(key);
        }
        
        ID id;
        // check cache first
        Map cached = null;
        if (memCacheEntity) {
            try {
                cached = memCache.getAll(keys);
                
                // found entities should not be queried
                keys.removeAll(cached.keySet());
                
                // add to returnValue
                Set<Entry<Key, T>> cachedEntries = cached.entrySet();
                for (Entry<Key, T> entry : cachedEntries) {
                    id = convert(entry.getKey());
                    returnValue.put(id, entry.getValue());
                }
                entitiesCached = cached.size();
            } catch (CacheException ignore) {
            }
        }
        
        // cache miss?
        if (!keys.isEmpty()) {
            final DatastoreService datastore = getDatastoreService();
            final Map<Key, Entity> entities = datastore.get(keys);
            T domain;
            final Map<Key, T> toCache = new HashMap<Key, T>(entities.size());
            for(Entry<Key, Entity> entry : entities.entrySet()) {
                id = convert(entry.getKey());
                domain = createDomain(entry.getValue());
                returnValue.put(id, domain);
                toCache.put(entry.getKey(), domain);
            }

            if (memCacheEntity) {
                memCache.putAll(toCache);
            }
            entitiesQueried = entities.size();
        }
        
        LOG.debug("cached:{}, queried:{}", entitiesCached, entitiesQueried);
        return returnValue;
    }

    protected final String memCacheAllKey() {
        return String.format("%s.findAll()", getTableName());
    }
    
    /**
     * Overrides to implement MemCache
     * @return 
     */
    @Override
    public List<T> findAll() {
        if (memCacheAll) {
            final String memCacheKey = memCacheAllKey();
            List<T> returnValue = (List<T>) memCache.get(memCacheKey);
            LOG.debug("{} cached is {}", memCacheKey, null != returnValue);
            if (null == returnValue) {
                returnValue = super.findAll();
                memCache.put(memCacheKey, returnValue);
            }
            return returnValue;
        }
        return super.findAll();
    }
    
    public List<ID> findAllKeys() {
        PreparedQuery pq = prepare(true, Entity.KEY_RESERVED_PROPERTY, true);
        return asKeys(pq, -1, 0);
    }

    protected final void updateCache(Map<Key, T> domains) {
        LOG.debug("updating cache for {} {}", domains.size(), getTableName());
        if (!domains.isEmpty()) {
            // invalidate cache
            if (memCacheAll) {
                memCache.remove(memCacheAllKey());
            }

            if (memCacheEntity) {
                memCache.putAll(domains);
            }
        }
    }
    
    protected final void updateCache(Collection<Key> keys, Iterable<T> domains) {
        final Map<Key, T> cacheMap = new HashMap<Key, T>();
        
        if (memCacheEntity) {
            // update domains in cache
            final Iterator<Key> i = keys.iterator();
            Key key;
            for (T domain : domains) {
                key = i.next();
                cacheMap.put(key, domain);
            }
        }
        
        updateCache(cacheMap);
    }

    @Override
    protected List<Key> persistStep1(Iterable<T> domains) {
        final List<Key> keys = super.persistStep1(domains);
        
        updateCache(keys, domains);
        
        return keys;
    }

    @Override
    protected List<Key> updateStep1(Iterable<T> domains) {
        final List<Key> keys = super.updateStep1(domains);
        
        updateCache(keys, domains);
        
        return keys;
    }
    
    @Override
    protected final List<Key> persistByCore(Iterable<Entity> entities) {
        final DatastoreService datastore = getDatastoreService();
        List<Key> keys;
        try {
            keys = datastore.put(entities);
        }
        catch (DatastoreFailureException ex) {
            LoggerFactory.getLogger(AEDDaoImpl.class).warn("Re-trying with allocated ids: {}", ex.getMessage());
            // the id allocated for a new entity was already in use, please try again
            keys = new ArrayList<Key>();
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
        }
        return keys;
    }

    protected final List<Key> updateByCore(Iterable<Entity> entities) {
        return persistByCore(entities);
    }

    @Override
    public final void deleteByCore(Key primaryKey) {
        // trivial optimization
        final DatastoreService datastore = getDatastoreService();
        datastore.delete(primaryKey);
        if (memCacheEntity) {
            memCache.remove(primaryKey);
        }
        if (memCacheAll) {
            memCache.remove(memCacheAllKey());
        }
    }

    public final void deleteByCore(Iterable<Key> primaryKeys) {
        final DatastoreService datastore = getDatastoreService();
        datastore.delete(primaryKeys);
        if (memCacheEntity) {
            memCache.remove(primaryKeys);
        }
        if (memCacheAll) {
            memCache.remove(memCacheAllKey());
        }
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
    
    public QueryResultIterable<T> queryAll() {
        return queryBy(null, false, null);
    }

    protected QueryResultIterable<T> queryBy(String orderBy, boolean ascending, Key parentKey, Expression... filters) {
        PreparedQuery pq = prepare(false, parentKey, null, orderBy, ascending, filters);
        return new CursorIterable(asQueryResultIterable(pq));
    }

    public int update(Map<String, Object> values, Expression... expressions) {
        throw new UnsupportedOperationException("Not yet implemented for AED");
    }

    private static String blobKeyFormat = "%s";
    /**
     * Also supports Key
     * @param value
     * @return 
     */
    @Override
    protected String xmlFieldValue(Object value) {
        if (value instanceof Key) {
            return KeyFactory.keyToString((Key)value);
        }
        if (value instanceof BlobKey) {
            return String.format(blobKeyFormat, ((BlobKey)value).getKeyString());
        }
        return super.xmlFieldValue(value);
    }

    @Override
    protected void xmlGenerateFields(ContentHandler ch, T domain) throws SAXException {
        super.xmlGenerateFields(ch, domain);
        xmlGenerateField(ch, FIELD_NAME_PARENT, domain.getParentKey());
    }

    /**
     * Overrides to return queryAll()
     * @return a QueryResultIterable from queryAll()
     */
    @Override
    public Iterable<T> xmlFindAll() {
        return queryAll();
    }
    
    static class XmlArg {
        protected FileWriteChannel channel;
        protected AppEngineFile file;
        protected Writer writer;
        protected final Collection<String> blobUrls = new ArrayList<String>();
    }
    
    @Override
    public void xmlGenerateEntities(Writer willBeNull, Object appArg0, Iterable<T> cursor) throws SAXException, IOException, TransformerConfigurationException {
        final XmlArg arg = (XmlArg) appArg0;
        
        // create new
        final FileService fileService = FileServiceFactory.getFileService();
        arg.file = fileService.createNewBlobFile("text/xml", getTableName() + ".xml");
        arg.channel = fileService.openWriteChannel(arg.file, true);
        arg.writer = Channels.newWriter(arg.channel, "UTF-8");

        super.xmlGenerateEntities(arg.writer, appArg0, cursor);
        
        // close
        try {
            arg.writer.flush();
            arg.writer.close();
            arg.channel.closeFinally();
        }
        catch (ApiDeadlineExceededException ignore) {
            LOG.warn("{} when closing for {}", ignore.getMessage(), getTableName());
        }

        // format and add blobUrl
        final BlobKey blobKey = fileService.getBlobKey(arg.file);
        final String blobUrl = xmlFieldValue(blobKey.getKeyString());
        arg.blobUrls.add(blobUrl);
        
        LOG.info("   generated entities for {} on {}", getTableName(), blobUrl);
    }
    
    public class CursorIterable<T> implements QueryResultIterable<T> {
        final private QueryResultIterable<Entity> _iterable;

        public CursorIterable(QueryResultIterable<Entity> _iterable) {
            this._iterable = _iterable;
        }
        
        
        public QueryResultIterator<T> iterator() {
            return new CursorIterator<T>(_iterable.iterator());
        }
    }

    class CursorIterator<T> implements QueryResultIterator<T> {
        private final QueryResultIterator<Entity> _iterator;

        protected CursorIterator(QueryResultIterator<Entity> _iterator) {
            this._iterator = _iterator;
        }

        public boolean hasNext() {
            return _iterator.hasNext();
        }

        public T next() {
            return (T) createDomain(_iterator.next());
        }

        public void remove() {
            _iterator.remove();
        }

        public Cursor getCursor() {
            return _iterator.getCursor();
        }
    }
    
    protected abstract Key xmlCreateKey(String id, Key parentKey);
    
    protected void xmlPopulateProperty(Entity entity, String name, String value, Class clazz) {
        if (null != value) {
            if (Long.class.equals(clazz)) {
                entity.setProperty(name, Long.parseLong(value));
            }
            else if (Key.class.equals(clazz)) {
                entity.setProperty(name, KeyFactory.stringToKey(value));
            }
            else if (BlobKey.class.equals(clazz)) {
                entity.setProperty(name, new BlobKey(value));
            }
            else {
                // default for Strings, converts to Text if length>500
                populate(entity, name, value);
            }
        }
    }
    
    protected abstract void xmlPopulateProperties(Entity entity, Properties properties);

    @Override
    protected T xmlCreateDomain(Properties properties) {
        final String id = properties.getProperty(ATTR_ID);
        final String parentKeyString = properties.getProperty(FIELD_NAME_PARENT);
        final Key parentKey = null != parentKeyString ? KeyFactory.stringToKey(parentKeyString) : null;
        final Key primaryKey = xmlCreateKey(id, parentKey);

        final Entity entity = new Entity(primaryKey);
        
        xmlPopulateProperties(entity, properties);
        
        final T domain = createDomain(entity);
        return domain;
    }
    
    public static BlobKey xmlWriteToBlobs(Dao... daos) throws IOException, SAXException, TransformerConfigurationException {
        final XmlArg arg = new XmlArg();
        
        // setup XML transformer, then for each dao invoke xmlGenerateEntities
        xmlGenerateEntityDaos(arg.writer, arg, daos);
        
        // now, write the master file of blobKeys
        FileService fileService = FileServiceFactory.getFileService();
        arg.file = fileService.createNewBlobFile("text/plain", "DaoEntities.txt");
        arg.channel = fileService.openWriteChannel(arg.file, true);
        arg.writer = Channels.newWriter(arg.channel, "UTF-8");
        final PrintWriter pw = new PrintWriter(arg.writer);
        
        for (String blobUrl : arg.blobUrls) {
            pw.println(blobUrl);
        }
        
        pw.flush();
        pw.close();
        arg.channel.closeFinally();
        final BlobKey blobKey = fileService.getBlobKey(arg.file);
        
        return blobKey;
    }
    
    public static void xmlParseFromBlobs(String baseUrl, String masterBlobKey, Dao... daos) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
        
        // first, read the master file of blobKeys
        final URL url = new URL(baseUrl + masterBlobKey);
        final InputStream in = url.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        final Collection<String> blobKeys = new ArrayList<String>();
        String s;
        while (null != (s = reader.readLine())) {
            blobKeys.add(s);
        }
        reader.close();
        
        // now, for each dao / XML file, parse and persist
        xmlPersistBlobs(baseUrl, blobKeys, daos);
    }
    
    public AEDDaoImpl getMardaoParentDao() {
        return mardaoParentDao;
    }

    public void setMardaoParentDao(AEDDaoImpl parentDao) {
        this.mardaoParentDao = parentDao;
    }

    public static Collection<AEDDaoImpl> getApplicationDaos() {
        return applicationDaos;
    }

    public static String getBlobKeyFormat() {
        return blobKeyFormat;
    }

    public static void setBlobKeyFormat(String format) {
        blobKeyFormat = format;
    }

    public void setMemCacheEntity(boolean memCacheEntity) {
        this.memCacheEntity = memCacheEntity;
    }

    public void setMemCacheAll(boolean memCacheAll) {
        this.memCacheAll = memCacheAll;
    }

}
