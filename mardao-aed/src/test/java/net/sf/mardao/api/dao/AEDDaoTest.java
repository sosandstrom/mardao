/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.dao;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import junit.framework.TestCase;

/**
 *
 * @author os
 */
public class AEDDaoTest extends TestCase {
    final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());
    
    AEDDaoImpl dao;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper.setUp();
        dao = new BookDaoImpl();
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
        assertNotNull(actual.getCursorKey());
        assertNotNull(actual.getItems());
        assertTrue(actual.getItems().isEmpty());
    }
}
