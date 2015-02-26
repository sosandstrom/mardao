package net.sf.mardao.dao;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by sosandstrom on 2015-02-22.
 */
public abstract class AbstractSupplier<K, RV, WV, T> implements Supplier<K, RV, WV, T> {

    @Override
    public K insertValue(T tx, K key, WV value) throws IOException {
        return writeValue(tx, key, value);
    }

    /** Void for most suppliers */
    @Override
    public void setPrimaryKey(WV value, Mapper mapper, String column, K primaryKey, Object Entity) {
    }

    /** Void for most suppliers */
    @Override
    public void setParentKey(WV value, Mapper mapper, String column, K parentKey, Object Entity) {
    }

    protected abstract Object getReadObject(RV value, String column);

    @Override
    public ByteBuffer getByteBuffer(RV value, String column) {
        return (ByteBuffer) getReadObject(value, column);
    }

    @Override
    public Float getFloat(RV value, String column) {
        return (Float) getReadObject(value, column);
    }

    @Override
    public Boolean getBoolean(RV value, String column) {
        return (Boolean) getReadObject(value, column);
    }

    @Override
    public Integer getInteger(RV value, String column) {
        return (Integer) getReadObject(value, column);
    }

    @Override
    public String getString(RV value, String column) {
        return (String) getReadObject(value, column);
    }

    @Override
    public K getParentKey(RV value, String column) {
        return (K) getReadObject(value, column);
    }

    @Override
    public K getKey(RV value, String column) {
        return (K) getReadObject(value, column);
    }

    @Override
    public Long getLong(RV value, String column) {
        return (Long) getReadObject(value, column);
    }

    @Override
    public Date getDate(RV value, String column) {
        return (Date) getReadObject(value, column);
    }

    @Override
    public Collection getCollection(RV value, String column) {
        return (Collection) getReadObject(value, column);
    }


    protected Object getWriteObject(WV value, String column) {
        return getReadObject((RV) value, column);
    }

    @Override
    public Collection getWriteCollection(WV value, String column) {
        return (Collection) getWriteObject(value, column);
    }

    @Override
    public Date getWriteDate(WV value, String column) {
        return (Date) getWriteObject(value, column);
    }

    @Override
    public Long getWriteLong(WV value, String column) {
        return (Long) getWriteObject(value, column);
    }

    @Override
    public K getWriteKey(WV value, String column) {
        return (K) getWriteObject(value, column);
    }

    @Override
    public K getWriteParentKey(WV value, String column) {
        return (K) getWriteObject(value, column);
    }

    @Override
    public String getWriteString(WV value, String column) {
        return (String) getWriteObject(value, column);
    }

    @Override
    public Integer getWriteInteger(WV value, String column) {
        return (Integer) getWriteObject(value, column);
    }

    @Override
    public Boolean getWriteBoolean(WV value, String column) {
        return (Boolean) getWriteObject(value, column);
    }

    @Override
    public Float getWriteFloat(WV value, String column) {
        return (Float) getWriteObject(value, column);
    }

    @Override
    public ByteBuffer getWriteByteBuffer(WV value, String column) {
        return (ByteBuffer) getWriteObject(value, column);
    }

    protected abstract void setObject(WV value, String column, Object o);

    @Override
    public void setCollection(WV value, String column, Collection c) {
        setObject(value, column, c);
    }

    @Override
    public void setDate(WV value, String column, Date d) {
        setObject(value, column, d);
    }

    @Override
    public void setLong(WV value, String column, Long l) {
        setObject(value, column, l);
    }

    @Override
    public void setString(WV value, String column, String s) {
        setObject(value, column, s);
    }

    @Override
    public void setInteger(WV value, String column, Integer i) {
        setObject(value, column, i);
    }

    @Override
    public void setBoolean(WV value, String column, Boolean b) {
        setObject(value, column, b);
    }

    @Override
    public void setFloat(WV value, String column, Float f) {
        setObject(value, column, f);
    }

    @Override
    public void setByteBuffer(WV value, String column, ByteBuffer b) {
        setObject(value, column, b);
    }
}
