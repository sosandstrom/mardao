/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.List;
import net.sf.mardao.api.domain.AndroidLongEntity;

/**
 *
 * @author mardao
 */
public abstract class AbstractDatabaseHelper extends SQLiteOpenHelper {
    /** Use this tag for logging */
    public static final String TAG = AbstractDatabaseHelper.class.getSimpleName();
    
    /** The database name is 'mardao' */
    protected static final String DATABASE_NAME = "mardao";

    private static final ThreadLocal<SQLiteDatabase> database = new ThreadLocal<SQLiteDatabase>();
    private static final ThreadLocal<Integer> connDepth = new ThreadLocal<Integer>();
    
    /** Override to change database name from 'mardao' */
    protected String getDatabaseName() {
        return DATABASE_NAME;
    }
    
    protected AbstractDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.i(TAG, "DatabaseHelper.<init>");
    }
    
    protected SQLiteDatabase beginTransaction() {
        final SQLiteDatabase dbCon = getDbConnection();
        dbCon.beginTransaction();
        return dbCon;
    }
    
    protected void commitTransaction(SQLiteDatabase dbCon) {
        try {
            if (dbCon.inTransaction()) {
                dbCon.setTransactionSuccessful();
                dbCon.endTransaction();
            }
        }
        finally {
            releaseDbConnection();
        }
    }
    
    protected void rollbackTransaction(SQLiteDatabase dbCon) {
        try {
            if (dbCon.inTransaction()) {
                dbCon.endTransaction();
            }
        }
        finally {
            releaseDbConnection();
        }
    }
    
    protected SQLiteDatabase getDbConnection() {
        SQLiteDatabase dbCon = database.get();
        Integer depth = connDepth.get();
        
        if (null == dbCon) {
            // create new connection
            dbCon = getWritableDatabase();
            depth = 0;
            database.set(dbCon);
        }
        else {
            if (null == depth) {
                depth = 1;
            }
        }
        
        // increase depth
        connDepth.set(depth + 1);
        
        return dbCon;
    }

    protected void releaseDbConnection() {
        Integer depth = connDepth.get();

        // close when depth is going back to 0 only
        if (null == depth || depth.equals(1)) {
            connDepth.remove();
            
            final SQLiteDatabase dbCon = database.get();
            if (null != dbCon) {
                database.remove();
                dbCon.close();
            }
        }
        else {
            // decrease depth
            connDepth.set(depth - 1);
        }
    }
    
    public static final <T extends AndroidLongEntity> List<Long> asKeys(List<T> entities) {
        return AndroidDaoImpl.asKeys(entities);
    }
}
