package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.mardao.api.domain.CreatedUpdatedEntity;
import net.sf.mardao.api.domain.JDBCLongEntity;
import net.sf.mardao.api.domain.JDBCPrimaryKeyEntity;
import net.sf.mardao.api.jdbc.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public abstract class JDBCDaoImpl<T extends JDBCPrimaryKeyEntity<ID>, ID extends Serializable, P extends Serializable> extends
        DaoImpl<T, ID, P, Entity, ID> {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /** the Spring SimpleJDBC template */
    protected SimpleJdbcTemplate                 template;

    /** Spring inserter */
    protected SimpleJdbcInsert                   insert;

    /** Used to generate unique ids */
    protected DataFieldMaxValueIncrementer incrementer;

    protected JDBCDaoImpl(Class<T> type) {
        super(type);
    }

    @Override
    protected ID convert(ID key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected T createDomain(Entity entity) {
        // do null-check here only :-)
        if (null != entity)  {
            try {
                return getRowMapper().mapRow(entity, -1);
            } catch (SQLException ex) {
                LOG.error("When creating domain", ex);
            }
        }
        return null;
    }

    @Override
    protected Expression createEqualsFilter(String fieldName, Object param) {
        // TODO Auto-generated method stub
        return null;
    }

    protected abstract SimpleJdbcInsert createJdbcInsert(DataSource dataSource);

    @Override
    public ID createKey(P parentKey, ID simpleKey) {
        return simpleKey;
    }

    @Override
    protected ID createKey(T domain) {
        return domain.getPrimaryKey();
    }

    public final int deleteAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteByCore(Iterable<ID> primaryKeys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T findByPrimaryKey(P parentKey, ID primaryKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<ID, T> findByPrimaryKeys(P parentKey, Iterable<ID> primaryKeys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<ID> findAllKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected T findBy(Expression... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<T> findBy(String orderBy, boolean ascending, int limit, int offset, Serializable parentKey, Expression... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<T> findBy(Map<String, Object> filters, String primaryOrderBy, boolean primaryDirection,
            String secondaryOrderBy, boolean secondaryDirection, int limit, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<ID> findCoreKeysByParent(Serializable parentKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<ID> findKeysBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<ID> findKeysByParent(Serializable parentKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected abstract ParameterizedRowMapper<T> getRowMapper();

    @SuppressWarnings("unchecked")
    public List<ID> persist(Iterable<T> domains) {
        final Date date = new Date();
        final List<ID> ids = new ArrayList<ID>();
        final List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
        Entity entity;
        for(T domain : domains) {
            LOG.debug("persist {}", domain);

            // get id from next in sequence:
            ID id = domain.getPrimaryKey();
            if (null == id && domain instanceof JDBCLongEntity) {
                Long lid = incrementer.nextLongValue();
                id = (ID) lid;
                persistUpdateKeys(domain, id);
            }

            entity = createEntity(domain);
            persistUpdateDates(domain, entity, date);
            properties.add(entity);
            ids.add(domain.getPrimaryKey());
        }
        final Map<String, Object>[] batch = (Map<String, Object>[]) properties.toArray();
        insert.executeBatch(batch);
        return ids;
        
    }

    @Override
    protected ID persistEntity(Entity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void persistUpdateDates(CreatedUpdatedEntity domain, Entity entity, Date date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void populate(Entity entity, Map<String, Object> nameValuePairs) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
        this.incrementer = incrementer;
    }

    @Override
    protected List<ID> updateByCore(Iterable<Entity> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
