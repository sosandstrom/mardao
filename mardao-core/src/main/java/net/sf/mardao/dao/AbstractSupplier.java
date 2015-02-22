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

    @Override
    public Collection getWriteCollection(WV value, String column) {
        return getCollection((RV) value, column);
    }

    @Override
    public Date getWriteDate(WV value, String column) {
        return getDate((RV) value, column);
    }

    @Override
    public Long getWriteLong(WV value, String column) {
        return getLong((RV) value, column);
    }

    @Override
    public K getWriteKey(WV value, String column) {
        return getKey((RV) value, column);
    }

    @Override
    public K getWriteParentKey(WV value, String column) {
        return getParentKey((RV) value, column);
    }

    @Override
    public String getWriteString(WV value, String column) {
        return getString((RV) value, column);
    }

    @Override
    public Integer getWriteInteger(WV value, String column) {
        return getInteger((RV) value, column);
    }

    @Override
    public Boolean getWriteBoolean(WV value, String column) {
        return getBoolean((RV) value, column);
    }

    @Override
    public Float getWriteFloat(WV value, String column) {
        return getFloat((RV) value, column);
    }

    @Override
    public ByteBuffer getWriteByteBuffer(WV value, String column) {
        return getByteBuffer((RV) value, column);
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
