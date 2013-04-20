/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.mardao.test;

import java.util.Date;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import net.sf.mardao.core.domain.ExtendsBean;

/**
 *
 * @author sosandstrom
 */
public class CrudTest 
        extends AbstractDatabaseInstrumentationTestCase {
//        extends InstrumentationTestCase {
    
    protected Long create() {
        return dbHelper.createExtendsBean(getName());
    }

    public void testCreate() {
        Long id = create();
        assertNotNull(id);
    }
    
    public void testRead() {
        Long id = create();
        ExtendsBean actual = dbHelper.getExtendsBean(id);
        assertNotNull(actual);
        Date created = actual.getCreatedDate();
        assertNotNull(created);
        assertEquals(created, actual.getUpdatedDate());
        assertEquals(getName(), actual.getMessage());
    }
    
    public void testUpdate() throws InterruptedException {
        Long id = create();
        ExtendsBean first = dbHelper.getExtendsBean(id);
        Date created = first.getCreatedDate();
        assertNotNull(created);
        assertEquals(created, first.getUpdatedDate());
        assertEquals(getName(), first.getMessage());
        Thread.sleep(1000L);
        
        first.setMessage("updated");
        dbHelper.getExtendsBeanDao().update(first);
        assertTrue(created.before(first.getUpdatedDate()));
        
        ExtendsBean second = dbHelper.getExtendsBeanDao().findByPrimaryKey(id);
        assertTrue(second.getCreatedDate().before(second.getUpdatedDate()));
        assertEquals("updated", second.getMessage());
    }
    
    public void testDelete() {
        Long id = create();
        ExtendsBean actual = dbHelper.getExtendsBean(id);
        assertNotNull(actual);
        
        dbHelper.getExtendsBeanDao().delete(actual);
        actual = dbHelper.getExtendsBean(id);
        assertNull(actual);
    }
}
