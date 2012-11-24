package net.sf.mardao.test;

import javax.sql.DataSource;
import junit.framework.TestCase;
import net.sf.mardao.core.dao.DaoImpl;
import net.sf.mardao.test.dao.InMemoryDataFieldMaxValueIncrementer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 *
 * @author os
 */
public class BasicDaoTest extends TestCase {
    
    static final Logger LOG = LoggerFactory.getLogger(BasicDaoTest.class);
    
    final DataSource dataSource = new SingleConnectionDataSource(
            "jdbc:h2:mem:typeDaoTest", "mardao", "jUnit", true);
    final InMemoryDataFieldMaxValueIncrementer incrementer = 
            new InMemoryDataFieldMaxValueIncrementer();
    BookDaoImpl dao;
    
    @Override
    protected void setUp() throws Exception {
        LOG.info("===== setUp() {} =====", getName());
        super.setUp();
        dao = new BookDaoImpl();
        dao.setDataSource(dataSource);
        dao.setJdbcIncrementer(incrementer);
        LOG.info("--- setUp() {} ---", getName());
    }
    
    @Override
    protected void tearDown() throws Exception {
        LOG.info("--- tearDown() {} ---", getName());
        final Iterable<Long> ids = dao.queryAllKeys();
        dao.delete(null, ids);
        super.tearDown();
    }
    
    public void testQueryOneItem() {
        final String NAME = "John Doe";
        DaoImpl.setPrincipalName(NAME);
        final Book expected = new Book();
        expected.setId(42L);
        expected.setTitle("Hello Galaxy");
        dao.persist(expected);
        assertNotNull(expected.getCreatedDate());
        assertEquals(expected.getCreatedDate(), expected.getUpdatedDate());
        DaoImpl.setPrincipalName(null);
        
        Iterable<Book> books = dao.queryByTitle(expected.getTitle());
        assertNotNull(books);
        
        final Book actual = books.iterator().next();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getCreatedDate(), actual.getUpdatedDate());
        assertEquals(NAME, actual.getCreatedBy());
        assertEquals(NAME, actual.getUpdatedBy());
    }

    public void testPersistAndGenerate() {
        final String NAME = "John Doe";
        DaoImpl.setPrincipalName(NAME);
        final Book expected = new Book();
        expected.setTitle("Hello Galaxy");
        dao.persist(expected);
        assertNotNull(expected.getId());
        assertNotNull(expected.getCreatedDate());
        assertEquals(expected.getCreatedDate(), expected.getUpdatedDate());
        DaoImpl.setPrincipalName(null);
        
        final Book actual = dao.findByPrimaryKey(expected.getId());
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getCreatedDate(), actual.getUpdatedDate());
        assertEquals(NAME, actual.getCreatedBy());
        assertEquals(NAME, actual.getUpdatedBy());
    }

}
