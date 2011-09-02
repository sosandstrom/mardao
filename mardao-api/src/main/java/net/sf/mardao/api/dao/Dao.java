package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sf.mardao.api.domain.PrimaryKeyEntity;

/**
 * 
 * @author os
 * 
 * @param <T>
 *            The domain object type
 * @param <ID>
 *            The domain object's simple key type
 * @param <P>
 *            The domain object's parent key type
 * @param <C>
 *            The Core implementation key type, e.g. Key for AED
 */
public interface Dao<T extends PrimaryKeyEntity, ID extends Serializable, P extends Serializable, C extends Serializable> {
    void delete(T entity);

    void delete(Iterable<T> entities);

    int deleteAll();

    void delete(P parentKey, ID simpleKey);

    void delete(P parentKey, Iterable<ID> simpleKeys);

    void deleteByCore(C primaryKey);

    void deleteByCore(Iterable<C> primaryKeys);

    List<T> findAll();

    T findByPrimaryKey(ID primaryKey);

    T findByPrimaryKey(P parentKey, ID primaryKey);

    T findByPrimaryKey(Object parentKey, ID primaryKey);

    Map<ID, T> findByPrimaryKeys(Iterable<ID> primaryKeys);

    Map<ID, T> findByPrimaryKeys(P parentKey, Iterable<ID> primaryKeys);

    Map<ID, T> findByPrimaryKeys(Object parentKey, Iterable<ID> primaryKeys);

    List<ID> findAllKeys();

    ID persist(T entity);

    List<ID> persist(Iterable<T> entities);

    ID update(T entity);

    List<ID> update(Iterable<T> entities);

    // --- utility methods ----

    List<String> getColumnNames();

    String getTableName();

    String getPrimaryKeyColumnName();

    C createKey(ID simpleKey);

    C createKey(P parentKey, ID simpleKey);

    C createKey(Object parentKey, ID simpleKey);

    Iterable<C> createKeys(Iterable<ID> rootKeys);

    Iterable<C> createKeys(P parentKey, Iterable<ID> simpleKeys);

    Iterable<C> createKeys(Object parentKey, Iterable<ID> simpleKeys);

}
