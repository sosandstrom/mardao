package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sf.mardao.api.domain.PrimaryKeyEntity;

public interface Dao<T extends PrimaryKeyEntity, ID extends Serializable> {
    void delete(T entity);

    void delete(Iterable<T> entities);

    int deleteAll();

    List<T> findAll();

    T findByPrimaryKey(ID primaryKey);

    T findByPrimaryKey(Object parentKey, ID primaryKey);

    Map<ID, T> findByPrimaryKeys(Iterable<ID> primaryKeys);

    Map<ID, T> findByPrimaryKeys(Object parentKey, Iterable<ID> primaryKeys);

    List<ID> findAllKeys();

    void persist(T entity);

    void update(T entity);

    List<String> getColumnNames();

    String getTableName();

    Object getPrimaryKeyColumnName();
}
