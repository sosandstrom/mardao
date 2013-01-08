/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.TypeDaoImpl;
import net.sf.mardao.core.dao.DaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class TypeDaoTest extends TestCase {
    
    static final Logger LOG = LoggerFactory.getLogger(TypeDaoTest.class);
    
    final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());
    
    TypeDaoImpl<Book, Long> dao;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper.setUp();
        dao = new BookDaoImpl();
        LOG.info("--- setUp() {} ---", getName());
    }
    
    @Override
    protected void tearDown() throws Exception {
        helper.tearDown();
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
        final StringBuffer name = new StringBuffer();
        for (int i = 0; i < 500; i++) {
            name.append(NAME);
        }
        DaoImpl.setPrincipalName(NAME);
        final Book expected = new Book();
        expected.setId(42L);
        expected.setTitle(name.toString());
        dao.persist(expected);
        assertNotNull(expected.getCreatedDate());
        assertEquals(NAME, expected.getCreatedBy());
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
        
        Serializable cursorString = null;
        CursorPage<Book, Long> page;
        for (int p = 0; p < 11; p++) {
            page = dao.queryPage(10, cursorString);
            LOG.info(String.format("queried page %d with cursor %s, got %d items", p, cursorString, page.getItems().size()));
            assertEquals("For page " + p, 10 == p ? 0 : 10, page.getItems().size());
            
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

    public void testWriteAsCsv() throws FileNotFoundException, IOException {
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

        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File f = new File(tmp, "typeDao.csv");
        LOG.info("writing CSV to {}", f.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(f);
        dao.writeAsCsv(fos, new String[] {/**"id", */ "title"}, dao, null, null, false, null, false);
        fos.close();
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
}
