/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.core.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQuery;
import java.io.Serializable;

/**
 * 
 * @author os
 */
public class CursorIterableFactory<T, ID extends Serializable> implements CursorFactory {

    private final TypeDaoImpl<T, ID> dao;

    public CursorIterableFactory(final TypeDaoImpl<T, ID> dao) {
        this.dao = dao;
    }

    public Cursor newCursor(final SQLiteDatabase sqld, final SQLiteCursorDriver sqlcd, final String string, final SQLiteQuery sqlq) {
        return new CursorIterable<T, ID>(sqld, sqlcd, string, sqlq, dao);
    }

}
