package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.List;

public interface Dao<T, ID extends Serializable> {
    void delete(T entity);

    void delete(List<ID> primaryKeys);

    int deleteAll();

    List<T> findAll();

    T findByPrimaryKey(ID primaryKey);

    T findByPrimaryKey(Object parentKey, ID primaryKey);

    void persist(T entity);

    void update(T entity);
}
