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
        if (null != value) {
            if (value instanceof Long) {
                contentValues.put(name, (Long)value);
            }
            else if (value instanceof String) {
                contentValues.put(name, (String)value);
            }
            else if (value instanceof Date) {
                contentValues.put(name, ((Date)value).getTime());
            }
            else if (value instanceof Boolean) {
                contentValues.put(name, (Boolean) value);
            }
            else if (value instanceof Double) {
                contentValues.put(name, (Double) value);
            }
            else if (value instanceof Float) {
                contentValues.put(name, (Float) value);
            }
            else {
                throw new UnsupportedOperationException("Not supported yet " + name + ":" + value.getClass().getName());
            }
        }
        else {
            contentValues.remove(name);
        }
    }
    
    public Object getProperty(String name) {
        return contentValues.get(name);
    }
}
