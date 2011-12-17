package net.sf.mardao.api.dao;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.util.Collections;
import java.util.Iterator;
import net.sf.mardao.api.domain.AndroidLongEntity;
import net.sf.mardao.api.domain.AndroidPrimaryKeyEntity;
import net.sf.mardao.api.domain.CreatedUpdatedEntity;

public abstract class AndroidDaoImpl<T extends AndroidLongEntity> extends
        DaoImpl<T, Long, Long, AndroidEntity, Long> {
    protected final String TAG = getClass().getSimpleName();
    
    protected final SQLiteDatabase database;
    
    protected final CursorFactory cursorFactory = new CursorIterableFactory(this);

    protected AndroidDaoImpl(Class<T> type, SQLiteDatabase database) {
        super(type);
        Log.d(TAG, "<init>");
        this.database = database;
    }

    @Override
    protected Long convert(Long key) {
        return key;
    }

    protected static final void convertCreatedUpdatedDates(AndroidEntity from, AndroidPrimaryKeyEntity domain) {
        if (null != domain._getNameCreatedDate()) {
            domain._setCreatedDate(new Date((Long)from.getProperty(domain._getNameCreatedDate())));
        }

        if (null != domain._getNameUpdatedDate()) {
            domain._setUpdatedDate(new Date((Long)from.getProperty(domain._getNameUpdatedDate())));
        }
    }
    
    protected abstract T createDomain(Cursor cursor);

    protected AndroidEntity createEntity(Long primaryKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Expression createEqualsFilter(String fieldName, Object param) {
        // TODO Auto-generated method stub
        return null;
    }

    public Long createKey(Long parentKey, Long simpleKey) {
        // TODO Auto-generated method stub
        return null;
    }

    protected Long createKey(T entity) {
        return entity.getSimpleKey();
    }

    public final int deleteAll() {
        return database.delete(getTableName(), "1", null);
    }

    @Override
    public void deleteByCore(Long primaryKey) {
        final String whereArgs[] = {primaryKey.toString()};
        Log.d(TAG, "delete " + getTableName() + " WHERE _id = " + primaryKey.toString());
        database.delete(getTableName(), "_id = ?", whereArgs);
    }

    public void deleteByCore(Iterable<Long> primaryKeys) {
        StringBuffer sb = new StringBuffer();
        Long id;
        for (Iterator<Long> i = primaryKeys.iterator(); i.hasNext(); ) {
            id = i.next();
            sb.append(id);
            sb.append(i.hasNext() ? "," : "");
        }
        Log.d(TAG, "delete WHERE _id IN " + sb.toString());
        final String whereArgs[] = {sb.toString()};
        database.delete(getTableName(), "_id IN (?)", whereArgs);
    }

    public T findByPrimaryKey(Long parentKey, Long primaryKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<Long, T> findByPrimaryKeys(Long parentKey, Iterable<Long> primaryKeys) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Long> findAllKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected T findBy(Expression... filters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<T> findBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        final List<T> returnValue = new ArrayList<T>();

        for (T domain : queryBy(orderBy, ascending, limit, offset, filters)) {
            returnValue.add(domain);
        }
        
        return returnValue;
    }

    @Override
    protected List<T> findBy(Map<String, Object> filters, String primaryOrderBy, boolean primaryDirection,
            String secondaryOrderBy, boolean secondaryDirection, int limit, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<Long> findCoreKeysByParent(Long parentKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<Long> findKeysBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<Long> findKeysByParent(Long parentKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Long> persist(Iterable<T> domains) {
        Log.d(TAG, "persist(Iterable)");
        final List<Long> ids = new ArrayList<Long>();
        Long id;
        for(T domain : domains) {
            id = persist(domain);
            ids.add(id);
        }
        return ids;
    }

    @Override
    public Long persist(T domain) {
        final AndroidEntity entity = createEntity(domain);
        final Long id = persistEntity(entity);
        persistUpdateKeys(domain, id);
        return id;
    }
    
    @Override
    protected Long persistEntity(AndroidEntity entity) {
        Long id = -1L;
        try {
            id = database.insertOrThrow(getTableName(), null, entity.getContentValues());
        }
        catch (SQLiteException e) {
            Log.d(TAG, e.getMessage() + " updating existing row");
            id = updateByCore(entity);
        }
        return id;
    }

    @Override
    protected final void persistUpdateDates(CreatedUpdatedEntity domain, AndroidEntity entity, Date date) {

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
    protected void persistUpdateKeys(T domain, Long key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void populate(AndroidEntity entity, String name, Object value) {
        if (null != entity && null != name) {
            entity.setProperty(name, value);
        }
    }
    
    public CursorIterable<T> queryAll() {
        return queryBy(null, false, -1, 0);
    }
    
    protected CursorIterable<T> queryBy(String columnName, Object value) {
        return queryBy(null, false, -1, 0, new Expression(columnName, "=", value));
    }    

    protected CursorIterable<T> queryBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        return (CursorIterable<T>) queryBy(false, orderBy, ascending, limit, offset, filters);
    }
    
    protected Cursor queryKeysBy(String columnName, Object value) {
        return queryKeysBy(null, false, -1, 0, new Expression(columnName, "=", value));
    }

    protected Cursor queryKeysBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        return queryBy(true, orderBy, ascending, limit, offset, filters);
    }

    private Cursor queryBy(boolean keysOnly, String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        // TODO: 
        CursorFactory factory = keysOnly ? null : cursorFactory;
        String[] columns = {getPrimaryKeyColumnName()};
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
            sb.append("?");
            sArgs.add(String.valueOf(filter.getOperand()));
            selection = sb.toString();
        }
        final String[] selectionArgs = sArgs.isEmpty() ? null : sArgs.toArray(new String[0]);
        Log.d("queryBy", "WHERE " + selection);
        final String orderByClause = null != orderBy ? orderBy + (ascending ? " ASC" : " DESC") : null;
        final String limitClause = 0 < limit ? "LIMIT " + limit + (0 < offset ? " OFFSET " + offset : "") : null;
        CursorIterable<T> cursor = (CursorIterable<T>) database.queryWithFactory(
                factory, true, getTableName(), 
                columns, selection, selectionArgs, 
                null, null, orderByClause, limitClause);
        return cursor;
    }
    
    protected List<Long> updateByCore(Iterable<AndroidEntity> entities) {
        final List<Long> ids = new ArrayList<Long>();
        
        for (AndroidEntity entity : entities) {
            ids.add(updateByCore(entity));
        }
        
        return ids;
    }

    protected Long updateByCore(AndroidEntity entity) {
        Long id = -1L;
        try {
            id = entity.getContentValues().getAsLong(getPrimaryKeyColumnName());
            String whereArgs[] = {id.toString()};
            database.update(getTableName(), entity.getContentValues(), "WHERE _id = ", whereArgs);
        }
        catch (SQLiteException e2) {
            Log.e(TAG, "SQLiteException" + e2.getMessage() + e2.toString());
            id = -1L;
        }
        return id;
    }

}
