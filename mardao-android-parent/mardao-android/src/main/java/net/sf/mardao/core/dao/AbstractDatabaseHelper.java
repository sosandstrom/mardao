/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * @author mardao
 */
public abstract class AbstractDatabaseHelper extends SQLiteOpenHelper {
	
    /** Use this tag for logging */
    public static final String TAG = AbstractDatabaseHelper.class.getSimpleName();
    
    /** The database name is 'mardao' */
    protected static final String DATABASE_NAME = "mardao";

    /** for singleton database connection strategy */
    private static SQLiteDatabase _db = null;
    
    protected AbstractDatabaseHelper(Context context, String databaseName) {
    	super(context, databaseName, null, 1);
    	Log.i(TAG, "DatabaseHelper.<init>");
    }
    
    protected AbstractDatabaseHelper(Context context) {
    	this(context, DATABASE_NAME);
    }
    
    protected SQLiteDatabase beginTransaction() {
        final SQLiteDatabase dbCon = getDbConnection();
        dbCon.beginTransaction();
        return dbCon;
    }
    
    protected void commitTransaction(SQLiteDatabase dbCon) {
        if (dbCon.inTransaction()) {
            dbCon.setTransactionSuccessful();
            dbCon.endTransaction();
        }
    }
    
    protected void rollbackTransaction(SQLiteDatabase dbCon) {
        if (dbCon.inTransaction()) {
            dbCon.endTransaction();
        }
    }
    
    protected SQLiteDatabase getDbConnection() {
        // singleton strategy
        if (null == _db) {
            _db = getWritableDatabase();
        }
        return _db;
    }

    protected void closeDbConnection() {
        if (null != _db) {
            _db.close();
            _db = null;
        }
    }
}
