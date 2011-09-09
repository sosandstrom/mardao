package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.mardao.api.domain.CreatedUpdatedEntity;
import net.sf.mardao.api.domain.JDBCPrimaryKeyEntity;
import net.sf.mardao.api.jdbc.Entity;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public abstract class JDBCDaoImpl<T extends JDBCPrimaryKeyEntity<ID>, ID extends Serializable> extends
        DaoImpl<T, ID, Long, Entity, Long> {

    /** the Spring SimpleJDBC template */
    protected SimpleJdbcTemplate                 template;

    /** Spring inserter */
    protected SimpleJdbcInsert                   insert;

    /** Used to generate unique ids */
    protected final DataFieldMaxValueIncrementer incrementer;

    protected JDBCDaoImpl(Class<T> type, DataFieldMaxValueIncrementer incrementer) {
        super(type);
        this.incrementer = incrementer;
    }

    @Override
    protected ID convert(Long key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected T createDomain(Entity entity) {
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

    protected abstract SimpleJdbcInsert createJdbcInsert(DataSource dataSource);

    public Long createKey(Long parentKey, ID simpleKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Long createKey(T entity) {
        // TODO Auto-generated method stub
        return null;
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

    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<ID> persist(Iterable<T> domains) {
        final List<ID> ids = new ArrayList<ID>();
        final List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
        Entity entity;
        for(T domain : domains) {
            LOG.debug("persist {}", domain);

            // get id from next in sequence:
            Long id = domain.getPrimaryKey();
            if (null == id) {
                id = incrementer.nextLongValue();
                domain.setPrimaryKey(id);
            }

            entity = createEntity(domain);
            properties.add(entity.getParameters());
            ids.add(domain.getSimpleKey());
        }
        final Map<String, Object>[] batch = (Map<String, Object>[]) properties.toArray();
        insert.executeBatch(batch);
        return ids;
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

    /**
     * Used for DataSource injection
     * 
     * @param dataSource
     *            the DataSource to inject
     */
    public void setDataSource(DataSource dataSource) {
        LOG.debug("setDataSource {} {}", ((org.apache.commons.dbcp.BasicDataSource) dataSource).getUrl(), getTableName());
        template = new SimpleJdbcTemplate(dataSource);

        // create the JDBC insert with specific columns and table name:
        insert = createJdbcInsert(dataSource);
    }

    @Override
    protected List<Long> updateByCore(Iterable<Entity> entities) {
        // TODO Auto-generated method stub
        return null;
    }

}
