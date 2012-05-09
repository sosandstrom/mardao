package net.sf.mardao.api.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.mardao.api.domain.AndroidLongEntity;
import net.sf.mardao.api.domain.AndroidPrimaryKeyEntity;
import net.sf.mardao.api.domain.CreatedUpdatedEntity;
import net.sf.mardao.manytomany.dao.AndroidManyToManyDaoBean;
import net.sf.mardao.manytomany.domain.AndroidManyToMany;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public abstract class AndroidDaoImpl<T extends AndroidLongEntity> extends DaoImpl<T, Long, Long, AndroidEntity, Long> {
    protected final String                 TAG           = getClass().getSimpleName();

    protected final AbstractDatabaseHelper databaseHelper;
    protected final CursorFactory          cursorFactory = new CursorIterableFactory<T>(this);

    protected AndroidDaoImpl(final Class<T> type, final AbstractDatabaseHelper helper) {
        super(type);
        this.databaseHelper = helper;
        Log.d(TAG, "<init>");
    }

    protected final synchronized SQLiteDatabase getDbConnection() {
        return databaseHelper.getDbConnection();
    }

    protected final synchronized void releaseDbConnection() {
        databaseHelper.releaseDbConnection();
    }

    public static final <T extends AndroidLongEntity> List<Long> asKeys(final List<T> entities) {
        final List<Long> keys = new ArrayList<Long>(entities.size());

        for (T e : entities) {
            keys.add(e.getSimpleKey());
        }

        return keys;
    }

    @Override
    protected Long convert(final Long key) {
        return key;
    }

    protected List<T> convert(final CursorIterable<T> cursor) {
        final List<T> returnValue = new ArrayList<T>();

        for (T domain : cursor) {
            returnValue.add(domain);
        }

        return returnValue;

    }

    protected List<Long> convert(final Cursor cursor) {
        final List<Long> returnValue = new ArrayList<Long>();

        if (cursor.moveToFirst()) {
            do {
                returnValue.add(cursor.getLong(0));
            }
            while (cursor.moveToNext());
        }

        return returnValue;

    }

    protected static final void convertCreatedUpdatedDates(final AndroidEntity from, final AndroidPrimaryKeyEntity<Long> domain) {
        String nameCreatedDate = domain._getNameCreatedDate();
        if (null != nameCreatedDate) {
            Long createdDate = (Long) from.getProperty(nameCreatedDate);
            if (null != createdDate) {
                domain._setCreatedDate(new Date(createdDate));
            }
        }

        String nameUpdatedDate = domain._getNameUpdatedDate();
        if (null != nameCreatedDate) {
            Long updatedDate = (Long) from.getProperty(nameUpdatedDate);
            if (null != updatedDate) {
                domain._setUpdatedDate(new Date(updatedDate));
            }
        }
    }

    protected abstract T createDomain(Cursor cursor);

    @Override
    protected AndroidEntity createEntity(final Long primaryKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Expression createEqualsFilter(final String fieldName, final Object param) {
        return new Expression(fieldName, " = ? ", param);
    }

    protected Expression createInFilter(final String fieldName, final Object param) {
        return new Expression(fieldName, " IN (?) ", param);
    }

    public Long createKey(final Long parentKey, final Long simpleKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Long createKey(final T entity) {
        return entity.getSimpleKey();
    }

    public final int deleteAll() {
        return deleteWithConnection("1", null);
    }

    private final int deleteWithConnection(final String whereClause, final String whereArgs[]) {
        final SQLiteDatabase dbCon = getDbConnection();
        try {
            return dbCon.delete(getTableName(), whereClause, whereArgs);
        }
        finally {
            releaseDbConnection();
        }
    }

    @Override
    public void deleteByCore(final Long primaryKey) {
        final String whereArgs[] = {primaryKey.toString()};
        Log.d(TAG, "delete " + getTableName() + " WHERE _id = " + primaryKey.toString());
        deleteWithConnection("_id = ?", whereArgs);
    }

    public void deleteByCore(final Iterable<Long> primaryKeys) {
        StringBuffer sb = new StringBuffer();
        Long id;
        for (Iterator<Long> i = primaryKeys.iterator(); i.hasNext();) {
            id = i.next();
            sb.append(id);
            sb.append(i.hasNext() ? "," : "");
        }
        Log.d(TAG, "delete WHERE _id IN " + sb.toString());
        final String whereArgs[] = {sb.toString()};
        deleteWithConnection("_id IN (?)", whereArgs);
    }

    public Collection<T> findByManyToMany(final AndroidManyToManyDaoBean m2mDao, final boolean owning, final Long foreignId) {
        final List<AndroidManyToMany> mappings = owning ? m2mDao.findByInverseId(foreignId) : m2mDao.findByOwningId(foreignId);
        final List<Long> ids = new ArrayList<Long>(mappings.size());
        for (AndroidManyToMany m : mappings) {
            ids.add(owning ? m.getOwningId() : m.getInverseId());
        }
        return findByPrimaryKeys(ids).values();
    }

    public T findByPrimaryKey(final Long parentKey, final Long primaryKey) {
        return findUniqueBy(getPrimaryKeyColumnName(), primaryKey);
    }

    public Map<Long, T> findByPrimaryKeys(final Long parentKey, final Iterable<Long> primaryKeys) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Long> findAllKeys() {
        return findKeysBy(null, false, -1, 0);
    }

    @Override
    protected T findBy(final Expression... filters) {
        List<T> list = findBy(null, false, 1, 0, filters);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    protected List<T> findBy(final String orderBy, final boolean ascending, final int limit, final int offset,
            final Long parentKey, final Expression... filters) {
        return findBy(this, orderBy, ascending, limit, offset, parentKey, filters);
    }

    protected static <T extends AndroidLongEntity> List<T> findBy(final AndroidDaoImpl<T> dao, final String orderBy,
            final boolean ascending, final int limit, final int offset, final Long parentKey, final Expression... filters) {
        dao.getDbConnection();
        try {
            @SuppressWarnings("unchecked")
            CursorIterable<T> cursor = (CursorIterable<T>) queryBy(dao, false, orderBy, ascending, limit, offset, filters);
            List<T> list = dao.convert(cursor);
            cursor.close();
            return list;
        }
        finally {
            dao.releaseDbConnection();
        }

    }

    @Override
    protected List<T> findBy(final Map<String, Object> filters, final String primaryOrderBy, final boolean primaryDirection,
            final String secondaryOrderBy, final boolean secondaryDirection, final int limit, final int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<Long> findCoreKeysByParent(final Long parentKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<Long> findKeysBy(final String orderBy, final boolean ascending, final int limit, final int offset,
            final Expression... filters) {
        Cursor cursor = queryKeysBy(orderBy, ascending, limit, offset, filters);
        List<Long> list = convert(cursor);
        cursor.close();
        return list;
    }

    @Override
    protected List<Long> findKeysByParent(final Long parentKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Long> persist(final Iterable<T> domains) {
        Log.d(TAG, "persist(Iterable)");
        final List<Long> ids = new ArrayList<Long>();
        Long id;
        for (T domain : domains) {
            id = persist(domain);
            ids.add(id);
        }
        return ids;
    }

    @Override
    public Long persist(final T domain) {
        final AndroidEntity entity = createEntity(domain);
        final Long id = persistEntity(entity);
        persistUpdateKeys(domain, id);
        return id;
    }

    @Override
    protected Long persistEntity(final AndroidEntity entity) {
        Long id = -1L;
        final SQLiteDatabase dbCon = getDbConnection();
        try {
            id = dbCon.insertOrThrow(getTableName(), null, entity.getContentValues());
        }
        catch (SQLiteException e) {
            Log.d(TAG, e.getMessage() + " updating existing row");
            id = updateByCore(entity);
        }
        finally {
            releaseDbConnection();
        }
        return id;
    }

    @Override
    protected final void persistUpdateDates(final CreatedUpdatedEntity domain, final AndroidEntity entity, final Date date) {

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

    @Override
    protected void persistUpdateKeys(final T domain, final Long key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void populate(final AndroidEntity entity, final String name, final Object value) {
        if (null != entity && null != name) {
            entity.setProperty(name, value);
        }
    }

    public CursorIterable<T> queryAll() {
        return queryBy(null, false, -1, 0);
    }

    public CursorIterable<T> queryByPrimaryKeys(final Collection<Long> primaryKeys) {
        return queryBy(null, false, -1, 0, createInFilter(getPrimaryKeyColumnName(), primaryKeys));
    }

    protected CursorIterable<T> queryBy(final String columnName, final Object value) {
        return queryBy(null, false, -1, 0, createEqualsFilter(columnName, value));
    }

    protected CursorIterable<T> queryBy(final String orderBy, final boolean ascending, final int limit, final int offset,
            final Expression... filters) {
        return (CursorIterable<T>) queryBy(false, orderBy, ascending, limit, offset, filters);
    }

    protected Cursor queryKeysBy(final String columnName, final Object value) {
        return queryKeysBy(null, false, -1, 0, createEqualsFilter(columnName, value));
    }

    protected Cursor queryKeysBy(final String orderBy, final boolean ascending, final int limit, final int offset,
            final Expression... filters) {
        return queryBy(true, orderBy, ascending, limit, offset, filters);
    }

    private Cursor queryBy(final boolean keysOnly, final String orderBy, final boolean ascending, final int limit,
            final int offset, final Expression... filters) {
        return queryBy(this, keysOnly, orderBy, ascending, limit, offset, filters);
    }

    private static Cursor queryBy(final AndroidDaoImpl dao, final boolean keysOnly, final String orderBy,
            final boolean ascending, final int limit, final int offset, final Expression... filters) {
        // TODO:
        CursorFactory factory = keysOnly ? null : dao.cursorFactory;
        String[] columns = {dao.getPrimaryKeyColumnName()};
        if (!keysOnly) {
            columns = null;
        }
        String selection = null;
        StringBuffer sb = new StringBuffer();
        ArrayList<String> sArgs = new ArrayList<String>();
        for (Expression filter : filters) {
            if (0 < sb.length()) {
                sb.append(" AND ");
            }
            sb.append(filter.getColumn());
            sb.append(filter.getOperation());
            // included in operation to support IN better
            // sb.append("?");
            sArgs.add(buildOperand(filter.getOperand()));
            selection = sb.toString();
        }
        final String[] selectionArgs = sArgs.isEmpty() ? null : sArgs.toArray(new String[sArgs.size()]);
        final String orderByClause = null != orderBy ? orderBy + (ascending ? " ASC" : " DESC") : null;
        final String limitClause = 0 < limit ? String.valueOf(limit) + (0 < offset ? "," + offset : "") : null;

        final SQLiteDatabase dbCon = dao.getDbConnection();
        try {
            Cursor cursor = dbCon.queryWithFactory(factory, true, dao.getTableName(), columns, selection, selectionArgs, null,
                    null, orderByClause, limitClause);
            Log.d("queryBy", dao.getTableName() + " WHERE " + selection + " returns " + cursor.getCount());
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
            return builder.toString();
        }
        else {
            return String.valueOf(operand);
        }
    }

    @Override
    protected List<Long> updateByCore(final Iterable<AndroidEntity> entities) {
        final List<Long> ids = new ArrayList<Long>();

        for (AndroidEntity entity : entities) {
            ids.add(updateByCore(entity));
        }

        return ids;
    }

    protected Long updateByCore(final AndroidEntity entity) {
        final SQLiteDatabase dbCon = getDbConnection();
        Long id = -1L;
        try {
            id = entity.getContentValues().getAsLong(getPrimaryKeyColumnName());
            String whereArgs[] = {id.toString()};
            dbCon.update(getTableName(), entity.getContentValues(), "_id = ?", whereArgs);
        }
        catch (SQLiteException e2) {
            Log.e(TAG, "SQLiteException" + e2.getMessage() + e2.toString());
            id = -1L;
        }
        finally {
            releaseDbConnection();
        }
        return id;
    }

}
