package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.mardao.api.domain.CreatedUpdatedEntity;
import net.sf.mardao.api.domain.JDBCPrimaryKeyEntity;
import net.sf.mardao.api.jdbc.Entity;

public abstract class JDBCDaoImpl<T extends JDBCPrimaryKeyEntity<ID>, ID extends Serializable> extends
        DaoImpl<T, ID, Long, Entity, Long> {

    protected JDBCDaoImpl(Class<T> type) {
        super(type);
    }

    public final int deleteAll() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void deleteByCore(Iterable<Long> primaryKeys) {
        // TODO Auto-generated method stub

    }

    public T findByPrimaryKey(Long parentKey, ID primaryKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<ID, T> findByPrimaryKeys(Long parentKey, Iterable<ID> primaryKeys) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ID> findAllKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ID> persist(Iterable<T> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }

    public Long createKey(Long parentKey, ID simpleKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected T createDomain(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ID convert(Long key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Entity createEntity(ID primaryKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Entity createEntity(T domain) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Expression createEqualsFilter(String fieldName, Object param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Long createKey(T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected T findBy(Expression... filters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<T> findBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<T> findBy(Map<String, Object> filters, String primaryOrderBy, boolean primaryDirection,
            String secondaryOrderBy, boolean secondaryDirection, int limit, int offset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<Long> findCoreKeysByParent(Long parentKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<ID> findKeysBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<ID> findKeysByParent(Long parentKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Long persistEntity(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void persistUpdateDates(CreatedUpdatedEntity domain, Entity entity, Date date) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void populate(Entity entity, Map<String, Object> nameValuePairs) {
        // TODO Auto-generated method stub

    }

    @Override
    protected List<Long> updateByCore(Iterable<Entity> entities) {
        // TODO Auto-generated method stub
        return null;
    }

}
