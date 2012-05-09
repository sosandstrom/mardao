/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.dao;

import net.sf.mardao.api.domain.AndroidLongEntity;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQuery;

/**
 * 
 * @author os
 */
public class CursorIterableFactory<T extends AndroidLongEntity> implements CursorFactory {

    private final AndroidDaoImpl<T> dao;

    public CursorIterableFactory(final AndroidDaoImpl<T> dao) {
        this.dao = dao;
    }

    public Cursor newCursor(final SQLiteDatabase sqld, final SQLiteCursorDriver sqlcd, final String string, final SQLiteQuery sqlq) {
        return new CursorIterable<T>(sqld, sqlcd, string, sqlq, dao);
    }

}
