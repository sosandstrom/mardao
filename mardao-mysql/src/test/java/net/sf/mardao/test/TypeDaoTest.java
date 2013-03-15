package net.sf.mardao.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import junit.framework.TestCase;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.TypeDaoImpl;
import net.sf.mardao.core.dao.DaoImpl;
import net.sf.mardao.test.dao.InMemoryDataFieldMaxValueIncrementer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 *
 * @author os
 */
public class TypeDaoTest extends TestCase {
    final DataSource dataSource = new SingleConnectionDataSource(
            "jdbc:h2:mem:typeDaoTest", "mardao", "jUnit", true);
    final InMemoryDataFieldMaxValueIncrementer incrementer = 
            new InMemoryDataFieldMaxValueIncrementer();
    
    static final Logger LOG = LoggerFactory.getLogger(TypeDaoTest.class);
    
    TypeDaoImpl<Book, Long> dao;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dao = new BookDaoImpl();
        dao.setJdbcIncrementer(incrementer);
        dao.setDataSource(dataSource);
        dao.init();
        LOG.info("--- setUp() {} ---", getName());
    }
    
    @Override
    protected void tearDown() throws Exception {
        final Iterable<Long> ids = dao.queryAllKeys();
        dao.delete(null, ids);
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
    public void testQueryEmptyPage() {
        CursorPage actual = dao.queryPage(10, null);
        assertNotNull(actual);
        assertNull(actual.getCursorKey());
        assertNotNull(actual.getItems());
        assertTrue(actual.getItems().isEmpty());
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
        
        final CursorPage<Book, Long> page = dao.queryPage(10, null);
        assertNotNull(page);
        assertNotNull(page.getItems());
        assertFalse(page.getItems().isEmpty());
        final Book actual = page.getItems().iterator().next();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getCreatedDate(), actual.getUpdatedDate());
        assertEquals(NAME, actual.getCreatedBy());
        assertEquals(NAME, actual.getUpdatedBy());
    }

    public void testQueryOneItemGenerateId() {
        final Book expected = new Book();
        expected.setTitle("Hello Galaxy");
        dao.persist(expected);
        assertNotNull(expected.getCreatedDate());
        assertNotNull(expected.getId());
        assertEquals(expected.getCreatedDate(), expected.getUpdatedDate());
        
        final CursorPage<Book, Long> page = dao.queryPage(10, null);
        assertNotNull(page);
        assertNotNull(page.getItems());
        assertFalse(page.getItems().isEmpty());
        final Book actual = page.getItems().iterator().next();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getCreatedDate(), actual.getUpdatedDate());
        assertEquals(DaoImpl.PRINCIPAL_NAME_ANONYMOUS, actual.getCreatedBy());
        assertEquals(DaoImpl.PRINCIPAL_NAME_ANONYMOUS, actual.getUpdatedBy());
    }

    public void testQueryTenPages() {
        final String NAME = "John Doe";
        List<Book> batch = new ArrayList<Book>(100);
        DaoImpl.setPrincipalName(NAME);
        for (int i = 0; i < 100; i++) {
            Book expected = new Book();
            expected.setId(1000L+i);
            expected.setTitle("Hex: 0x" + Integer.toHexString(i));
            batch.add(expected);
        }
        dao.persist(batch);
        DaoImpl.setPrincipalName(null);
        
        String cursorString = null;
        CursorPage<Book, Long> page;
        for (int p = 0; p < 11; p++) {
            page = dao.queryPage(10, cursorString);
            LOG.info(String.format("queried page %d with cursor %s, got %d items", p, cursorString, page.getItems().size()));
            assertEquals("For page " + p, 10 == p ? 0 : 10, page.getItems().size());
            if (null == cursorString) {
                assertEquals(Integer.valueOf(100), page.getTotalSize());
            }
            else {
                assertNull(page.getTotalSize());
            }
            
            cursorString = page.getCursorKey();
        }
        
    }

    public void testQueryAll() {
        final String NAME = "John Doe";
        List<Book> batch = new ArrayList<Book>(115);
        DaoImpl.setPrincipalName(NAME);
        for (int i = 0; i < 115; i++) {
            Book expected = new Book();
            expected.setId(1000L+i);
            expected.setTitle("Hex: 0x" + Integer.toHexString(i));
            batch.add(expected);
        }
        dao.persist(batch);
        DaoImpl.setPrincipalName(null);

        int count = 0;
        for (Book book : dao.queryAll()) {
            assertNotNull(book);
            count++;
        }
        assertEquals(115, count);
        
        count = dao.count();
        assertEquals(115, count);
    }

    public void testQueryAllMemCache() {
        final String NAME = "John Doe";
        List<Book> batch = new ArrayList<Book>(115);
        DaoImpl.setPrincipalName(NAME);
        for (int i = 0; i < 115; i++) {
            Book expected = new Book();
            expected.setId(1000L+i);
            expected.setTitle("Hex: 0x" + Integer.toHexString(i));
            batch.add(expected);
        }
        dao.persist(batch);
        DaoImpl.setPrincipalName(null);

        int count = 0;
        for (Book book : dao.queryAll()) {
            assertNotNull(book);
            count++;
        }
        assertEquals(115, count);
        
        // and with memCache
        count = 0;
        for (Book book : dao.queryAll()) {
            assertNotNull(book);
            count++;
        }
        assertEquals(115, count);
    }

    public void testQueryAllKeys() {
        final String NAME = "John Doe";
        List<Book> batch = new ArrayList<Book>(115);
        DaoImpl.setPrincipalName(NAME);
        for (int i = 0; i < 115; i++) {
            Book expected = new Book();
            expected.setId(1000L+i);
            expected.setTitle("Hex: 0x" + Integer.toHexString(i));
            batch.add(expected);
        }
        dao.persist(batch);
        DaoImpl.setPrincipalName(null);

        int count = 0;
        for (Long id : dao.queryAllKeys()) {
            assertNotNull(id);
            count++;
        }
        assertEquals(115, count);
    }

    public void testFindByPrimaryKeys() {
        final String NAME = "John Doe";
        List<Book> batch = new ArrayList<Book>(115);
        DaoImpl.setPrincipalName(NAME);
        final Collection<Long> subset = new ArrayList<Long>();
        for (int i = 0; i < 115; i++) {
            Book expected = new Book();
            expected.setId(1000L+i);
            expected.setTitle("Hex: 0x" + Integer.toHexString(i));
            batch.add(expected);
            if (i < 51) {
                subset.add(expected.getId());
            }
        }
        dao.persist(batch);
        DaoImpl.setPrincipalName(null);

        int count = 0;
        for (Book b : dao.queryByPrimaryKeys(null, subset)) {
            assertNotNull(b);
            count++;
        }
        assertEquals(51, count);
    }
    
    public void testUpdate() {
        final String NAME = "John Doe";
        DaoImpl.setPrincipalName(NAME);
        final Book expected = new Book();
        expected.setTitle("Hello Galaxy");
        dao.persist(expected);
        assertNotNull(expected.getId());
        assertNotNull(expected.getCreatedDate());
        assertEquals(NAME, expected.getCreatedBy());
        assertEquals(expected.getCreatedDate(), expected.getUpdatedDate());
        assertEquals(NAME, expected.getUpdatedBy());
        
        final Date createdDate = expected.getCreatedDate();
        DaoImpl.setPrincipalName("Jane Doe");
        expected.setTitle("Updated Title");
        dao.update(expected);
        assertEquals(createdDate, expected.getCreatedDate());
        assertEquals(NAME, expected.getCreatedBy());
        assertTrue(createdDate.before(expected.getUpdatedDate()));
        assertEquals("Jane Doe", expected.getUpdatedBy());
        assertEquals("Updated Title", expected.getTitle());
        
        DaoImpl.setPrincipalName(null);
    }
}
