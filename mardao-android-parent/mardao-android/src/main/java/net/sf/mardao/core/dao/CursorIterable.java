package net.sf.mardao.core.dao;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author os
 */
public class CursorIterable<T, ID extends Serializable> extends SQLiteCursor implements Iterable<T> {

    private boolean           iterating   = false;
    private boolean           inIteration = false;

    private TypeDaoImpl<T, ID> dao;

    public CursorIterable(final SQLiteDatabase db, final SQLiteCursorDriver driver, final String editTable,
            final SQLiteQuery query) {
        super(db, driver, editTable, query);
    }

    public CursorIterable(final SQLiteDatabase db, final SQLiteCursorDriver driver, final String editTable,
            final SQLiteQuery query, final TypeDaoImpl<T, ID> dao) {
        super(db, driver, editTable, query);
        this.dao = dao;
    }

    public Iterator<T> iterator() {
        moveToFirst();
        iterating = true;
        return new CursorIterator();
    }

    @Override
    public synchronized boolean onMove(final int oldPosition, final int newPosition) {
        if (inIteration) {
            if (!iterating) {
                throw new ConcurrentModificationException();
            }
        }
        else {
            iterating = false;
        }
        return super.onMove(oldPosition, newPosition);
    }

    private class CursorIterator implements Iterator<T> {
        public CursorIterator() {
        }

        public boolean hasNext() {
            return !isAfterLast();
        }

        public synchronized T next() {
            try {
                // use cursor, create domain object, move to next
                if (isAfterLast()) {
                    return null;
                }
                T domain = dao.createDomain(CursorIterable.this);
                inIteration = true;
                moveToNext();
                inIteration = false;
                return domain;
            } catch (InstantiationException ex) {
                return null;
            } catch (IllegalAccessException ex) {
                return null;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
