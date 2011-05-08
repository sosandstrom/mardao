package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Dao<T, ID extends Serializable> {
    void delete(T entity);

    void delete(List<T> entities);

    int deleteAll();

    List<T> findAll();

    T findByPrimaryKey(ID primaryKey);

    T findByPrimaryKey(Object parentKey, ID primaryKey);

    Map<ID, T> findByPrimaryKeys(Iterable<ID> primaryKeys);

    Map<ID, T> findByPrimaryKeys(Object parentKey, Iterable<ID> primaryKeys);

    List<ID> findAllKeys();

    void persist(T entity);

    void update(T entity);
}
