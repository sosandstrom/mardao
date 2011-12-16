/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.dao;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import java.util.Iterator;
import net.sf.mardao.api.domain.AndroidLongEntity;

/**
 *
 * @author os
 */
public class CursorIterable<T extends AndroidLongEntity> extends SQLiteCursor implements Iterable<T> {

    private AndroidDaoImpl dao;
    
    public CursorIterable(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        super(db, driver, editTable, query);
    }
    
    public CursorIterable(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query, AndroidDaoImpl dao) {
        super(db, driver, editTable, query);
        this.dao = dao;
    }
    
    public Iterator<T> iterator() {
        moveToFirst();
        return new CursorIterator<T>();
    }

    private class CursorIterator<T extends AndroidLongEntity> implements Iterator<T> {
        public CursorIterator() {
        }

        public boolean hasNext() {
            boolean returnValue = !isAfterLast();
            if (!returnValue) {
                close();
            }
            return returnValue;
        }

        public T next() {
            // use cursor, create domain object, move to next
            if (isAfterLast()) {
                return null;
            }
            T domain = (T) dao.createDomain(CursorIterable.this);
            moveToNext();
            return domain;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
}
