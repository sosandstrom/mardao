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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
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
        LOG.info("+++ tearDown() {} +++", getName());
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

    public void testQueryOneItemWithNullFilter() {
        final String NAME = "John Doe";
        DaoImpl.setPrincipalName(NAME);
        final Book expected = new Book();
        expected.setTitle(NAME);
        dao.persist(expected);
        assertNotNull(expected.getId());
        assertNotNull(expected.getCreatedDate());
        assertEquals(NAME, expected.getCreatedBy());
        assertEquals(expected.getCreatedDate(), expected.getUpdatedDate());
        DaoImpl.setPrincipalName(null);
        
        final Iterable<Book> page = ((BookDaoImpl)dao).queryByTitleAppArg0(NAME, null);
        assertNotNull(page);
        final Book actual = page.iterator().next();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAppArg0(), actual.getAppArg0());
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

    public void testWhatsChanged() {
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
        Date since = new Date();
        
        // delete 15
        ArrayList<Long> firstIds = new ArrayList<Long>();
        for (int i = 40; i < 54; i++) {
            firstIds.add(1000L+i);
        }
        dao.delete(null, firstIds);
        dao.delete(1054L);
        
        // update another 15
        for (Book u : batch) {
            if (1055L <= u.getId() && u.getId() < 1070L) {
                u.setAppArg0("updated");
                dao.update(u);
            }
        }
        
        DaoImpl.setPrincipalName(null);

        CursorPage<Long, Long> updated = dao.whatsChanged(since, 10, null);
        assertEquals(10, updated.getItems().size());
        for (Long id : updated.getItems()) {
            assertTrue(1055L <= id);
            assertTrue(id < 1070L);
        }
        assertFalse(updated.getCursorKey().startsWith("audit-"));
        
        CursorPage<Long, Long> mixed = dao.whatsChanged(since, 10, updated.getCursorKey());
        assertEquals(10, mixed.getItems().size());
        for (Long id : mixed.getItems()) {
            assertTrue(1040L <= id);
            assertTrue(id < 1070L);
        }
        assertTrue(mixed.getCursorKey().startsWith("audit-"));
        
        CursorPage<Long, Long> deleted = dao.whatsChanged(since, 11, mixed.getCursorKey());
        assertEquals(10, deleted.getItems().size());
        for (Long id : deleted.getItems()) {
            assertTrue(1040L <= id);
            assertTrue(id < 1055L);
        }
        assertNull(deleted.getCursorKey());
    }

//    public void testWriteAsCsv() throws FileNotFoundException, IOException {
//        final String NAME = "John Doe";
//        List<Book> batch = new ArrayList<Book>(115);
//        DaoImpl.setPrincipalName(NAME);
//        for (int i = 0; i < 115; i++) {
//            Book expected = new Book();
//            expected.setId(1000L+i);
//            expected.setTitle("Hex: 0x" + Integer.toHexString(i));
//            batch.add(expected);
//        }
//        dao.persist(batch);
//        DaoImpl.setPrincipalName(null);
//
//        File tmp = new File(System.getProperty("java.io.tmpdir"));
//        File f = new File(tmp, "typeDao.csv");
//        LOG.info("writing CSV to {}", f.getAbsolutePath());
//        FileOutputStream fos = new FileOutputStream(f);
//        dao.writeAsCsv(fos, new String[] {/**"id", */ "title"}, dao, null, null, false, null, false);
//        fos.close();
//    }
//    
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
    
    public void testCreateDomainFromProperties() {
        Map<String, String> p = new HashMap<String, String>();
        p.put(BookDaoImpl.COLUMN_NAME_ID, "42");
        p.put(BookDaoImpl.COLUMN_NAME_TITLE, "MyTitle");
        p.put(dao.getCreatedByColumnName(), "Creator");
        p.put(dao.getCreatedDateColumnName(), "1");
        p.put(dao.getUpdatedByColumnName(), "Updator");
        p.put(dao.getUpdatedDateColumnName(), "123455678");
        
        Book actual = dao.createDomain(p);
        assertEquals((Long) 42L, actual.getId());
        assertEquals("MyTitle", actual.getTitle());
    }
    
    public void testPersistForFuture() {
        ArrayList<Book> books = new ArrayList<Book>();
        for (int i = 0; i < 23; i++) {
            Book b = new Book();
            b.setAppArg0(Integer.toString(i));
            b.setTitle(String.format("Title for %d", i));
            books.add(b);
        }
        
        Future<List<?>> future = dao.persistForFuture(books);
        Collection<Long> ids = dao.getSimpleKeys(future);
        assertEquals(23, ids.size());
        for (Long id : ids) {
            assertNotNull(id);
        }
        for (Book b : books) {
            assertNotNull(b.getId());
        }
    }
}
