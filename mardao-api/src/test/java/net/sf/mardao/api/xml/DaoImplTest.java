package net.sf.mardao.api.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import junit.framework.TestCase;
import net.sf.mardao.api.dao.Dao;
import net.sf.mardao.api.dao.DaoImpl;
import net.sf.mardao.api.domain.Trans;

import static org.easymock.EasyMock.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author os
 */
public class DaoImplTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(DaoImplTest.class);
    public static final String TABLE_NAME = "Trans";
    
    Dao daoMock;
    final Map<String, Dao> DAO_MAP = new HashMap<String, Dao>();
    
    public DaoImplTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        LOG.info("=== setUp() {} ===", getName());
        super.setUp();
        daoMock = createMock(Dao.class);
        DAO_MAP.clear();
        DAO_MAP.put(TABLE_NAME, daoMock);
    }
    
    protected void replayAll() {
        LOG.debug("--- replay {} ---", getName());
        replay(daoMock);
    }
    
    @Override
    protected void tearDown() throws Exception {
        LOG.info("=== tearDown() {} ===", getName());
        verify(daoMock);
        super.tearDown();
    }

    /**
     * Test of xmlPersistBlob method, of class DaoImpl.
     */
    public void testXmlPersistBlob() throws Exception {
        String blobUrl = "file:src/test/resources/Trans.xml";
        
        final Trans t1 = new Trans();
        expect(daoMock.xmlPersistEntity(notNull(Properties.class))).andReturn(t1).times(6);
        
        replayAll();
        
        DaoImpl.xmlPersistBlob(DAO_MAP, blobUrl);
    }

    /**
     * Test of xmlPersistBlobs method, of class DaoImpl.
     */
//    public void testXmlPersistBlobs() throws Exception {
//        System.out.println("xmlPersistBlobs");
//        String baseUrl = "";
//        Collection<String> blobKeys = null;
//        Dao[] daos = null;
//        DaoImpl.xmlPersistBlobs(baseUrl, blobKeys, daos);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
