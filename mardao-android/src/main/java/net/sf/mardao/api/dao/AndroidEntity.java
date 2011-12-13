/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.dao;

import java.io.Serializable;

import android.content.ContentValues;
import java.util.Date;

/**
 *
 * @author os
 */
public class AndroidEntity implements Serializable {
    private final ContentValues contentValues;
    
    public AndroidEntity() {
        this.contentValues = new ContentValues();
    }

    public ContentValues getContentValues() {
        return contentValues;
    }

    public void setProperty(String name, Object value) {
        if (value instanceof Long) {
            contentValues.put(name, (Long)value);
        }
        else if (value instanceof String) {
            contentValues.put(name, (String)value);
        }
        else if (value instanceof Date) {
            contentValues.put(name, ((Date)value).getTime());
        }
        else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    public Object getProperty(String name) {
        return contentValues.get(name);
    }
}
