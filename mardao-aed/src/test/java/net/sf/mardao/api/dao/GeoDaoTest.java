package net.sf.mardao.api.dao;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.mardao.api.CursorPage;
import net.sf.mardao.api.domain.DEmployee;
import net.sf.mardao.api.geo.DLocation;
import net.sf.mardao.api.geo.Geobox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class GeoDaoTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(GeoDaoTest.class);
    
    final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());
    
    GeneratedDEmployeeDao employeeDao;
    
    public GeoDaoTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LOG.info("=== setUp() " + getName() + " ===");
        helper.setUp();
        
        final GeneratedDEmployeeDaoImpl employeeImpl = new GeneratedDEmployeeDaoImpl();
        this.employeeDao = employeeImpl;
        
        populate();
        LOG.info("--- setUp() " + getName() + " ---");
    }
    
    protected void populate() {
        LOG.info("--- populate() " + getName() + " ---");
        
        Map<Long, DEmployee> employees = new HashMap<Long, DEmployee>();
        for (int i = 1; i < 132; i++) {
            DEmployee employee = new DEmployee();
            employee.setId(Long.valueOf(i));
            employee.setFingerprint(String.format("%dfingerprint%d", i, i));
            employee.setOfficeLocation(0 == i % 9 ? null : 
                    new DLocation(20.0f + 0.0001f * ((i%20) - 10),
                        110.0f + 0.0001f * ((i%13)-6)));
            employeeDao.persist(employee);
            employees.put(Long.valueOf(i), employee);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        LOG.info("--- tearDown() " + getName() + " ---");
        helper.tearDown();
        super.tearDown();
    }
    
    public void testQuery() {
        Serializable cursorString = null;
        
        CursorPage<DEmployee, Long> page = employeeDao.queryInGeobox(20f, 110f, Geobox.BITS_15_1224m, 60,
                null, false, null, false, cursorString);
    }
    
}
