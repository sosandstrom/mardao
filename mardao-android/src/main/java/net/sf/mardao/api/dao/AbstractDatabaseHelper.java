/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.dao;

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
            dbCon.close();
        }
    }
    
    protected void rollbackTransaction(SQLiteDatabase dbCon) {
        try {
            if (dbCon.inTransaction()) {
                dbCon.endTransaction();
            }
        }
        finally {
            dbCon.close();
        }
    }
    
    protected SQLiteDatabase getDbConnection() {
        return getWritableDatabase();
    }

}
