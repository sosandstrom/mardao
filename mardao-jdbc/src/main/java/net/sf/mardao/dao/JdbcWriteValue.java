package net.sf.mardao.dao;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sosandstrom on 2015-02-22.
 */
public class JdbcWriteValue {
    protected final Object entity;
    protected final Mapper mapper;
    protected final Map<String, Object> parameterMap = new TreeMap<String, Object>();

    public JdbcWriteValue(Object entity, Mapper mapper) {
        this.entity = entity;
        this.mapper = mapper;
    }
}
