package net.sf.mardao.core.dao;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.Date;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.geo.DLocation;

public abstract class TypeDaoImpl<T, ID extends Serializable> extends DaoImpl<T, ID, Long, CursorIterable, ContentValues, Long> {
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    protected final String                 TAG           = getClass().getSimpleName();
    protected static final String OPERATION_IN = " IN (%s)";

    protected static AbstractDatabaseHelper databaseHelper;
    protected final CursorFactory          cursorFactory = new CursorIterableFactory<T, ID>(this);

    protected TypeDaoImpl(final Class<T> type, Class<ID> idType) {
        super(type, idType);
        debug("<init>");
    }

    @Override
    protected void println(int priority, String format, Object... args) {
        Log.println(priority, TAG, String.format(format, args));
    }

    @Override
    protected void printStackTrace(int priority, String message, Throwable t) {
        Log.println(priority, TAG, message);
        Log.println(priority, TAG, Log.getStackTraceString(t));
    }
    
    protected final synchronized SQLiteDatabase getDbConnection() {
        return databaseHelper.getDbConnection();
    }

    protected final synchronized void releaseDbConnection() {
        databaseHelper.releaseDbConnection();
    }
    
    protected static String appendWhereFilters(ArrayList<String> sArgs, Filter... filters) {
        final StringBuffer sb = new StringBuffer();
        String selection = null;
        String operand;
        Object operation;
        for (Filter filter : filters) {
            if (0 < sb.length()) {
                sb.append(" AND ");
            }
            sb.append(filter.getColumn());
            
            operand = buildOperand(filter.getOperand());
            if (OPERATION_IN.equals(filter.getOperation())) {
                operation = String.format(OPERATION_IN, operand);
            }
            else {
                operation = filter.getOperation();
                sArgs.add(operand);
            }
            sb.append(operation);
            sb.append("?");
            selection = sb.toString();
        }
        return selection;
    }

    @Override
    protected ID coreToSimpleKey(ContentValues core) {
        if (null == core) {
            return null;
        }
        return (ID) core.getAsLong(getPrimaryKeyColumnName());
    }

    @Override
    protected ID coreKeyToSimpleKey(Long core) {
        return (ID) core;
    }

    @Override
    protected Long coreToParentKey(ContentValues core) {
        if (null == core) {
            return null;
        }
        return core.getAsLong(getParentKeyColumnName());
    }

    @Override
    protected Long coreKeyToParentKey(Long core) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int count(Object ancestorKey, Object simpleKey, Filter... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
//        StringBuffer sql = new StringBuffer();
//        sql.append("SELECT COUNT(");
//        sql.append(getPrimaryKeyColumnName());
//        sql.append(") FROM ");
//        sql.append(getTableName());
//        
//        ArrayList<String> selectionArgs = new ArrayList<String>();
//        appendWhereFilters(sql, selectionArgs, filters);
//        
//        final SQLiteDatabase dbCon = dao.getDbConnection();
//        try {
//            Cursor cursor = dbCon.queryWithFactory(cursorFactory, true, getTableName(), 
//                    new String[] {getPrimaryKeyColumnName()}, selection, selectionArgs, null,
//                    null, orderByClause, limitClause);
//            Log.d("queryBy", "sArgs=" + sArgs);
//            Log.d("queryBy", dao.getTableName() + " WHERE " + selection + " returns " + cursor.getCount());
//            return cursor;
//        }
//        finally {
//            dao.releaseDbConnection();
//        }
//        
//        final int count = jdbcTemplate.queryForInt(sql.toString(), params);
//        LOG.debug("{} returns {}", sql.toString(), count);
//        return count;
    }

    @Override
    protected ContentValues createCore(Object primaryKey) {
        final ContentValues core = new ContentValues();
        core.put(getPrimaryKeyColumnName(), (Long) primaryKey);
        return core;
    }

    @Override
    protected ContentValues createCore(Object parentKey, ID simpleKey) {
        return createCore(simpleKey);
    }

    @Override
    protected Long createCoreKey(Object parentKey, ID simpleKey) {
        return (Long) simpleKey;
    }
    
    public static void putString(String columnName, CursorIterable cursor, ContentValues core) {
        if (null != columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (-1 < columnIndex) {
                core.put(columnName, cursor.getString(columnIndex));
            }
        }
    }

    public static void putLong(String columnName, CursorIterable cursor, ContentValues core) {
        if (null != columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (-1 < columnIndex) {
                core.put(columnName, cursor.getLong(columnIndex));
            }
        }
    }
    
    protected Object getFromCursor(String columnName, CursorIterable cursor) {
        if (null != columnName) {
            final int columnIndex = cursor.getColumnIndex(columnName);
            if (-1 < columnIndex) {

                final Class clazz = getColumnClass(columnName);
                if (Double.class.equals(clazz)) {
                    return cursor.getDouble(columnIndex);
                }
                if (Float.class.equals(clazz)) {
                    return cursor.getFloat(columnIndex);
                }
                if (Long.class.equals(clazz)) {
                    return cursor.getLong(columnIndex);
                }
                if (Integer.class.equals(clazz)) {
                    return cursor.getInt(columnIndex);
                }
                if (Short.class.equals(clazz)) {
                    return cursor.getShort(columnIndex);
                }
                if (Byte.class.equals(clazz)) {
                    return (byte) cursor.getShort(columnIndex);
                }
                if (Date.class.equals(clazz)) {
                    String s = cursor.getString(columnIndex);
                    try {
                        return SDF.parse(s);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
                if (String.class.equals(clazz)) {
                    return cursor.getString(columnIndex);
                }
                if (Boolean.class.equals(clazz)) {
                    return 0 != cursor.getShort(columnIndex);
                }
                if (DLocation.class.equals(clazz)) {
                    String s = cursor.getString(columnIndex);
                    String latLong[] = s.split(",");
                    return new DLocation(Float.parseFloat(latLong[0]), Float.parseFloat(latLong[1]));
                }
            }
        }
        return null;
    }
    
    protected void setDomainFromCursor(T domain, String columnName, CursorIterable cursor) {
        final Object value = getFromCursor(columnName, cursor);
        if (null != value) {
            setDomainProperty(domain, columnName, value);
        }
    }

    /**
     * Invoked by CursorIterable to map from cursor to domain object
     * @param cursor
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public T createDomain(CursorIterable cursor) throws InstantiationException, IllegalAccessException {
        if (null == cursor) {
            return null;
        }
        
        final ID simpleKey = (ID) getFromCursor(getPrimaryKeyColumnName(), cursor);
        final Long parentKey = (Long) getFromCursor(getParentKeyColumnName(), cursor);
        
        final T domain = createDomain(parentKey, simpleKey);
        
        // created, updated
        setDomainFromCursor(domain, getCreatedByColumnName(), cursor);
        setDomainFromCursor(domain, getCreatedDateColumnName(), cursor);
        setDomainFromCursor(domain, getUpdatedByColumnName(), cursor);
        setDomainFromCursor(domain, getUpdatedDateColumnName(), cursor);

        // Domain Entity-specific properties
        for (String name : getColumnNames()) {
            setDomainFromCursor(domain, name, cursor);
        }

        return domain;
    }

    @Override
    protected String createMemCacheKey(Object parentKey, ID simpleKey) {
        return String.format("%s.%s.%s", getTableName(), parentKey, simpleKey);
    }

    @Override
    public Filter createEqualsFilter(String columnName, Object value) {
        if (null == value) {
            return new Filter(columnName, " IS ", "NULL");
        }
        return new Filter(columnName, "=", value);
    }

    @Override
    public Filter createGreaterThanOrEqualFilter(String columnName, Object value) {
        return new Filter(columnName, ">=", value);
    }

    @Override
    public Filter createInFilter(String fieldName, Collection param) {
        return new Filter.IN(fieldName, param);
    }

    public int deleteAll() {
        return deleteWithConnection(null, null);
    }

    @Override
    protected int doDelete(Object parentKey, Iterable<ID> simpleKeys) {
        StringBuffer sb = new StringBuffer();
        ID id;
        for (Iterator<ID> i = simpleKeys.iterator(); i.hasNext();) {
            id = i.next();
            sb.append(id);
            sb.append(i.hasNext() ? "," : "");
        }
        final String whereClause = String.format("%s IN (%s)", getPrimaryKeyColumnName(), sb.toString());
        debug("delete %s WHERE %s", getTableName(), whereClause);
        final String whereArgs[] = {};
        return deleteWithConnection(whereClause, whereArgs);
    }

    @Override
    protected int doDelete(Iterable<T> domains) {
        return doDelete(null, getSimpleKeys(domains));
    }

    @Override
    public void dropTable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected T doFindByPrimaryKey(Object parentKey, ID simpleKey) {
        return findUniqueBy(createEqualsFilter(getPrimaryKeyColumnName(), simpleKey));
    }

    @Override
    protected Future<?> doFindByPrimaryKeyForFuture(Object parentKey, ID simpleKeys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Future<List<Long>> doPersistCoreForFuture(Iterable<ContentValues> entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Future<?> doPersistCoreForFuture(ContentValues core) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Iterable<T> doQueryByPrimaryKeys(Object parentKey, Iterable<ID> simpleKeys) {
        return findBy(createInFilter(getPrimaryKeyColumnName(), asList(simpleKeys)));
    }

    @Override
    protected T findUniqueBy(Filter... filters) {
        List<T> list = findBy(filters);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    protected ID findUniqueKeyBy(Filter... filters) {
        Iterable<ID> itrbl = queryIterableKeys(0, 1, null, null, null, false, null, false, filters);
        Iterator<ID> itr = itrbl.iterator();
        return itr.hasNext() ? itr.next() : null;
    }
    
    @Override
    protected Object getCoreProperty(ContentValues core, String name, Class domainPropertyClass) {
        Object value = null;
        if (null != core && null != name) {
            value = core.get(name);
            if (null != value) {
                if (DLocation.class.equals(domainPropertyClass)) {
                    final String latLong = (String)value;
                    final int commaIndex = latLong.indexOf(',');
                    value = new DLocation(Float.parseFloat(latLong.substring(0, commaIndex)), 
                            Float.parseFloat(latLong.substring(commaIndex+1)));
                }
                else if (Date.class.equals(domainPropertyClass)) {
                    try {
                        value = SDF.parse((String)value);
                    } catch (ParseException ex) {
                        error(value.toString(), ex);
                    }
                }
            }
        }
        return value;
    }

    public String getKeyString(Object key) {
        return null != key ? key.toString() : null;
    }

    @Override
    public Object getParentKey(T domain) {
        return null;
    }

    @Override
    public Object getParentKey(Map<String, String> properties) {
        String value = properties.get(getParentKeyColumnName());
        return null != value ? Long.parseLong(value) : null;
    }

    @Override
    public Object getParentKeyByPrimaryKey(Object primaryKey) {
        return null;
    }

    @Override
    public Object getPrimaryKey(String keyString) {
        return null != keyString ? Long.parseLong(keyString) : null;
    }

    @Override
    public Object getPrimaryKey(T domain) {
        return getSimpleKey(domain);
    }

    @Override
    protected Collection<Long> persistCore(Iterable<ContentValues> itrbl) {
        ArrayList<Long> ids = new ArrayList<Long>();
        for (ContentValues core : itrbl) {
            ids.add(persistCore(core));
        }
        return ids;
    }

    @Override
    protected CursorPage<T, ID> queryPage(boolean keysOnly, int pageSize, 
            Object ancestorKey, Object primaryKey, 
            String primaryOrderBy, boolean primaryIsAscending, 
            String secondaryOrderBy, boolean secondaryIsAscending, 
            String cursorString, Filter... filters) {
        
        CursorPage<T, ID> page = new CursorPage<T, ID>();
        
        int offset = null != cursorString ? Integer.parseInt(cursorString) : 0;
        Iterable<T> itrbl = queryIterable(keysOnly, offset, pageSize, 
                            ancestorKey, primaryKey, 
                            primaryOrderBy, primaryIsAscending, secondaryOrderBy, secondaryIsAscending, 
                            filters);
        final ArrayList<T> items = new ArrayList<T>();
        for (T t : itrbl) {
            items.add(t);
        }
        page.setItems(items);
        if (pageSize == items.size()) {
            page.setCursorKey(Integer.toString(offset + pageSize));
        }
        
        return page;
    }

    @Override
    protected Iterable<ID> queryIterableKeys(int offset, int limit, 
            Object ancestorKey, Object primaryKey, 
            String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, 
            Filter... filters) {
        return queryIterable(true, limit, offset, ancestorKey, primaryKey, 
                primaryOrderBy, primaryIsAscending, secondaryOrderBy, secondaryIsAscending,
                filters);
    }
    
    @Override
    protected void setCoreProperty(Object core, String name, Object value) {
        if (null != name) {
            final ContentValues cv = (ContentValues) core;
            if (null == value) {
                cv.putNull(name);
                return;
            }
            if (value instanceof DLocation) {
                final DLocation location = (DLocation) value;
                cv.put(name, String.format("%f,%f", location.getLatitude(), location.getLongitude()));
                return;
            }
            if (value instanceof Date) {
                cv.put(name, SDF.format((Date) value));
                return;
            }
            
            cv.put(name, String.valueOf(value));
        }
    }

    @Override
    protected CursorPage<ID, ID> whatsDeleted(Date since, int pageSize, String cursorKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Iterable<T> domains) {
        Date now = new Date();
        for (T t : domains) {
            updateByCore(domainToCore(t, now));
        }
    }

    @Override
    public void update(T domain) {
        updateByCore(domainToCore(domain, new Date()));
    }

//    @Override
//    protected Long convert(final Long key) {
//        return key;
//    }
//
//    protected Map<Long, T> convertToMap(final CursorIterable<T> cursor) {
//        final Map<Long, T> returnValue = new TreeMap<Long,T>();
//
//        for (T domain : cursor) {
//            returnValue.put(domain.getSimpleKey(), domain);
//        }
//
//        return returnValue;
//
//    }
//
//    protected List<T> convert(final CursorIterable<T> cursor) {
//        final List<T> returnValue = new ArrayList<T>();
//
//        for (T domain : cursor) {
//            returnValue.add(domain);
//        }
//
//        return returnValue;
//
//    }

    protected List<ID> convert(final Cursor cursor) {
        final List returnValue = new ArrayList();

        if (cursor.moveToFirst()) {
            do {
                returnValue.add(cursor.getLong(0));
            }
            while (cursor.moveToNext());
        }

        return returnValue;

    }

//    protected static final void convertCreatedUpdatedDates(final AndroidEntity from, final AndroidPrimaryKeyEntity<Long> domain) {
//        String nameCreatedDate = domain._getNameCreatedDate();
//        if (null != nameCreatedDate) {
//            Long createdDate = (Long) from.getProperty(nameCreatedDate);
//            if (null != createdDate) {
//                domain._setCreatedDate(new Date(createdDate));
//            }
//        }
//
//        String nameUpdatedDate = domain._getNameUpdatedDate();
//        if (null != nameCreatedDate) {
//            Long updatedDate = (Long) from.getProperty(nameUpdatedDate);
//            if (null != updatedDate) {
//                domain._setUpdatedDate(new Date(updatedDate));
//            }
//        }
//    }
//
//    protected abstract T createDomain(Cursor cursor);
//
//    @Override
//    protected AndroidEntity createEntity(final Long primaryKey) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    protected Expression createEqualsFilter(final String fieldName, final Object param) {
//        return new Expression(fieldName, " = ? ", param);
//    }
//
//    protected Expression createInFilter(final String fieldName, final Object param) {
//        return new Expression(fieldName, OPERATION_IN, param);
//    }
//
//    public Long createKey(final Long parentKey, final Long simpleKey) {
//        return simpleKey;
//    }
//
//    @Override
//    protected Long createKey(final T entity) {
//        return entity.getSimpleKey();
//    }
//
//    public final int deleteAll() {
//        return deleteWithConnection("1", null);
//    }

    private final int deleteWithConnection(final String whereClause, final String whereArgs[]) {
        final SQLiteDatabase dbCon = getDbConnection();
        try {
                return dbCon.delete(getTableName(), whereClause, whereArgs);
        }
        finally {
            releaseDbConnection();
        }
    }

//    @Override
//    public void deleteByCore(final Long primaryKey) {
//        final String whereArgs[] = {primaryKey.toString()};
//        Log.d(TAG, "delete " + getTableName() + " WHERE _id = " + primaryKey.toString());
//        deleteWithConnection("_id = ?", whereArgs);
//    }
//
//    public void deleteByCore(final Iterable<Long> primaryKeys) {
//    }
    
//    protected Collection<Long> fetchKeysForManyToMany(final AndroidManyToManyDaoBean m2mDao, final boolean owning, final Long foreignId) {
//        final List<AndroidManyToMany> mappings = owning ? m2mDao.findByInverseId(foreignId) : m2mDao.findByOwningId(foreignId);
//        final List<Long> ids = new ArrayList<Long>(mappings.size());
//        Long id;
//        for (AndroidManyToMany m : mappings) {
//            id = owning ? m.getOwningId() : m.getInverseId();
////            Log.d("findByManyToMany", "owning=" + owning + ", " + m);
//            ids.add(id);
//        }
//        return ids;
//    }
//    
//    protected Collection<AndroidLongEntity> fetchForManyToMany(final AndroidManyToManyDaoBean m2mDao, final boolean owning, final Long foreignId) {
//        final Collection<Long> ids = fetchKeysForManyToMany(m2mDao, owning, foreignId);
//        
//        final List<AndroidLongEntity> returnValue = new ArrayList<AndroidLongEntity>(ids.size());
//        AndroidLongEntity domain;
//        for (Long id : ids) {
//            domain = new BasicLongEntity(id);
//            returnValue.add(domain);
//        }
//        
//        return returnValue;
//    }
//
//    protected Collection<T> findByManyToMany(final AndroidManyToManyDaoBean m2mDao, final boolean owning, final Long foreignId) {
//        final Collection<Long> ids = fetchKeysForManyToMany(m2mDao, owning, foreignId);
//        return findByPrimaryKeys(null, ids).values();
//    }

//    public T findByPrimaryKey(final Long parentKey, final Long primaryKey) {
//        return findUniqueBy(getPrimaryKeyColumnName(), primaryKey);
//    }
//
//    public Map<Long, T> findByPrimaryKeys(Long parentKey, final Iterable<Long> primaryKeys) {
//        getDbConnection();
//        try {
//            CursorIterable<T> cursor = (CursorIterable<T>) queryByPrimaryKeys(primaryKeys);
//            Map<Long, T> list = convertToMap(cursor);
//            cursor.close();
//            return list;
//        }
//        finally {
//            releaseDbConnection();
//        }
//    }
//
//    public List<Long> findAllKeys() {
//        return findKeysBy(null, false, -1, 0);
//    }

//    @Override
    protected List<T> findBy(final Filter... filters) {
        return findBy(null, false, -1, 0, null, filters);
    }

//    @Override
    protected List<T> findBy(final String orderBy, final boolean ascending, final int limit, final int offset,
            final Long parentKey, final Filter... filters) {
        return findBy(this, orderBy, ascending, limit, offset, parentKey, filters);
    }

    protected static <T, ID extends Serializable> List<T> findBy(final TypeDaoImpl<T, ID> dao, final String orderBy,
            final boolean ascending, final int limit, final int offset, final Long parentKey, final Filter... filters) {
        dao.getDbConnection();
        try {
            @SuppressWarnings("unchecked")
            CursorIterable<T, ID> cursor = (CursorIterable<T, ID>) queryBy(dao, false, 
                    orderBy, ascending, null, false, 
                    limit, offset, filters);
            List<T> list = asList(cursor);
            cursor.close();
            return list;
        }
        finally {
            dao.releaseDbConnection();
        }

    }
    
    public void onCreate(SQLiteDatabase sqldb) {
        final String createSql = createTable();
        info("onCreate() %s", createSql);
        sqldb.execSQL(createSql);
    }

    public void onUpgrade(SQLiteDatabase sqldb, int fromVersion, int toVersion) {
        info("onUpgrade(%s) from %d to %d",
                getTableName(), fromVersion, toVersion);
    }

//    @Override
    protected Long persistCore(final ContentValues core) {
        Long id = -1L;
        final SQLiteDatabase dbCon = getDbConnection();
        try {
            debug("persistEntity %s %s", getTableName(), core);
            id = dbCon.insertOrThrow(getTableName(), null, core);
        }
        catch (SQLiteException e) {
            debug("%s updating existing row", e.getMessage());
            id = updateByCore(core);
        }
        finally {
            releaseDbConnection();
        }
        return id;
    }

    @Override
    protected Iterable queryIterable(final boolean keysOnly, 
            final int limit, final int offset, 
            Object ancestorKey, Object primaryKey,
            final String orderBy, final boolean ascending, 
            final String secondaryOrderBy, final boolean secondaryIsAscending, 
            final Filter... filters) {
        return (CursorIterable) queryBy(this, keysOnly, 
                orderBy, ascending, 
                secondaryOrderBy, secondaryIsAscending,
                limit, offset, filters);
    }

    private static Cursor queryBy(final TypeDaoImpl dao, final boolean keysOnly, 
            final String orderBy, final boolean ascending, 
            final String secondaryOrderBy, final boolean secondaryIsAscending, 
            final int limit, final int offset, final Filter... filters) {
        // TODO:
        CursorFactory factory = keysOnly ? null : dao.cursorFactory;
        String[] columns = {dao.getPrimaryKeyColumnName()};
        if (!keysOnly) {
            columns = null;
        }

        ArrayList<String> sArgs = new ArrayList<String>();
        String selection = appendWhereFilters(sArgs, filters);
        
        final String[] selectionArgs = sArgs.isEmpty() ? null : sArgs.toArray(new String[sArgs.size()]);
        final String orderByClause = null != orderBy ? orderBy + (ascending ? " ASC" : " DESC") : null;
        final String limitClause = 0 < limit ? String.valueOf(limit) + (0 < offset ? "," + offset : "") : null;

        final SQLiteDatabase dbCon = dao.getDbConnection();
        try {
            System.out.println("factory=" + factory + ", columns=" + (null != columns ? columns[0] : "*"));
            Cursor cursor = dbCon.queryWithFactory(factory, true, dao.getTableName(), columns, selection, selectionArgs, null,
                    null, orderByClause, limitClause);
//            debug("queryBy sArgs=%s", sArgs);
//            debug("queryBy %s WHERE %s returns %d", dao.getTableName(), selection, cursor.getCount());
            System.out.println("sArgs=" + sArgs);
            System.out.println(dao.getTableName() + " WHERE " + selection + " returns " + cursor.getCount() + " in " + cursor);
            return cursor;
        }
        finally {
            dao.releaseDbConnection();
        }
    }

    private static String buildOperand(final Object operand) {
        if (operand instanceof Boolean) {
            return Boolean.TRUE.equals(operand) ? "1" : "0";
        }
        else if (operand instanceof Collection<?>) {
            StringBuilder builder = new StringBuilder();
            for (Object object : (Collection<?>) operand) {
                if (builder.length() > 0) {
                    builder.append(',');
                }
                builder.append(buildOperand(object));
            }
            final String returnValue = builder.toString();
//            Log.d("buildOperand", returnValue);
            return returnValue;
        }
        else {
            return String.valueOf(operand);
        }
    }
    
    public static ArrayList asList(Iterable itrbl) {
        final ArrayList ids = new ArrayList();
        for (Object i : itrbl) {
            ids.add(i);
        }
        return ids;
    }

    protected List<Long> updateByCore(final Iterable<ContentValues> entities) {
        final List<Long> ids = new ArrayList<Long>();

        for (ContentValues entity: entities) {
            ids.add(updateByCore(entity));
        }

        return ids;
    }

    protected Long updateByCore(final ContentValues entity) {
        final SQLiteDatabase dbCon = getDbConnection();
        Long id = -1L;
        try {
            id = entity.getAsLong(getPrimaryKeyColumnName());
            String whereArgs[] = {id.toString()};
            debug("updateByCore %s %s", getTableName(), entity);
            dbCon.update(getTableName(), entity, "_id = ?", whereArgs);
        }
        catch (SQLiteException e2) {
            error("SQLiteException %s: %s", e2.getMessage(), e2.toString());
            id = -1L;
        }
        finally {
            releaseDbConnection();
        }
        return id;
    }

    //    protected void updateManyToMany(final AndroidManyToManyDaoBean m2mDao,  boolean owning,
    //            final Long id, final Iterable<Long> relatedIds) {
    //        final SQLiteDatabase trans = databaseHelper.beginTransaction();
    //        try {
    //            final List<AndroidManyToMany> mappings = owning ? m2mDao.findByOwningId(id) :
    //                    m2mDao.findByInverseId(id);
    //
    //            // collect existing related ids:
    //            final Map<Long,AndroidManyToMany> existing = new HashMap<Long,AndroidManyToMany>(mappings.size());
    //            for (AndroidManyToMany m : mappings) {
    //                existing.put(owning ? m.getInverseId() : m.getOwningId(), m);
    //            }
    //
    //            // create new for missing
    //            AndroidManyToMany exist;
    //            final List<AndroidManyToMany> insert = new ArrayList<AndroidManyToMany>();
    //            for (Long r : relatedIds) {
    //                exist = existing.remove(r);
    //                if (null == exist) {
    //                    insert.add(new AndroidManyToMany(owning ? id : r, owning ? r : id));
    //                }
    //            }
    //            m2mDao.persist(insert);
    //
    //            // remove those not present in related
    //            final Collection<AndroidManyToMany> entities = existing.values();
    //            m2mDao.delete(null, asKeys(entities));
    //        }
    //        finally {
    //            databaseHelper.commitTransaction(trans);
    //        }
    //    }
    
    // ------------------ CREATE TABLE statements ------------------------------
    
    protected static final Properties DATA_TYPES_DEFAULT = new Properties();
    
    static {
        DATA_TYPES_DEFAULT.setProperty(Double.class.getName(), "REAL");
        DATA_TYPES_DEFAULT.setProperty(Float.class.getName(), "REAL");
        DATA_TYPES_DEFAULT.setProperty(Long.class.getName(), "INTEGER");
        DATA_TYPES_DEFAULT.setProperty(Integer.class.getName(), "INTEGER");
        DATA_TYPES_DEFAULT.setProperty(Short.class.getName(), "INTEGER");
        DATA_TYPES_DEFAULT.setProperty(Byte.class.getName(), "INTEGER");
        DATA_TYPES_DEFAULT.setProperty(Date.class.getName(), "DATETIME");
        DATA_TYPES_DEFAULT.setProperty(String.class.getName(), "VARCHAR");
        DATA_TYPES_DEFAULT.setProperty(Boolean.class.getName(), "NUMERIC");
        DATA_TYPES_DEFAULT.setProperty(DLocation.class.getName(), "VARCHAR(33)");
        DATA_TYPES_DEFAULT.setProperty("AUTO_INCREMENT", "AUTO_INCREMENT");
        DATA_TYPES_DEFAULT.setProperty("COLUMN_QUOTE", "`");
//        DATA_TYPES_DEFAULT.setProperty(String.class.getName(), "VARCHAR(500)");

    }
    
    protected String createTable() {
        final StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ");
        sql.append(getTableName());
        // column definitions
        sql.append(" (");

        sql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT");
        appendParentKeyColumnDefinition(sql);
        
        for (String columnName : getBasicColumnNames()) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName);
        }
        
        // foreign keys?
        for (String columnName : getManyToOneColumnNames()) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName);
        }
        
        appendConstraints(sql);
        
        sql.append(");");
        
        return sql.toString();
    }

    protected void appendParentKeyColumnDefinition(StringBuffer sql) {
        final String columnName = getParentKeyColumnName();
        if (null != columnName) {
            sql.append(", ");
            appendColumnDefinition(sql, columnName, true);
        }
    }
    
    protected void appendColumnDefinition(StringBuffer sql, String columnName, 
            boolean isPrimaryKey) {
        sql.append(columnName);
        sql.append(' ');
        final String className = getColumnClass(columnName).getName();
        String dataType = getDataType(className, isPrimaryKey);
        if (null == dataType) {
            dataType = getDataType(Long.class.getName());
        }
        sql.append(dataType);
    }
    
    protected void appendColumnDefinition(StringBuffer sql, String columnName) {
        appendColumnDefinition(sql, columnName, false);
    }
    
    protected void appendConstraints(StringBuffer sql) {
        
        // parent key
        if (null != getParentKeyColumnName()) {
            appendConstraint(sql, getParentKeyColumnName(), mardaoParentDao);
        }
        
        // ManyToOnes
        for (String columnName : getManyToOneColumnNames()) {
            appendConstraint(sql, columnName, getManyToOneDao(columnName));
        }
    }
    
    protected void appendConstraint(StringBuffer sql, String columnName, DaoImpl foreignDao) {
        sql.append(", ");
        sql.append("CONSTRAINT ");
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append("Fk");
        sql.append(getTableName());
        sql.append(columnName);
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(" FOREIGN KEY (");
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(columnName);
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(") REFERENCES ");
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(foreignDao.getTableName());
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append('(');
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(foreignDao.getPrimaryKeyColumnName());
        sql.append(getDataType("COLUMN_QUOTE"));
        sql.append(')');
    }
    
    protected String getDataType(String className) {
        return getDataType(className, false);
    }
    
    protected String getDataType(String className, boolean isPrimaryKey) {
        String returnValue = DATA_TYPES_DEFAULT.getProperty(className);

        return returnValue;
    }
    
    
    //  ----------------- getters and setters ----------------------------------
    
    public static void setDatabaseHelper(AbstractDatabaseHelper databaseHelper) {
        TypeDaoImpl.databaseHelper = databaseHelper;
    }
    
}
