package net.sf.mardao.api.dao;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.mardao.api.domain.DEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class GeneratedDaoTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(GeneratedDaoTest.class);
    
    final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());
    
    GeneratedDEmployeeDao employeeDao;
    
    public GeneratedDaoTest(String testName) {
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
            employee.setNickname(String.format("Nick%d", i % 11));
            if (1 < i) {
                employee.setManager(employees.get(Long.valueOf(i / 10)+1L));
            }
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
    
    public void testQueryAll() {
        for (DEmployee empl : employeeDao.queryAll()) {
            assertNotNull(empl.getFingerprint());
            long i = empl.getId();
            assertEquals(String.format("%dfingerprint%d", i, i), empl.getFingerprint());
            if (1 < i) {
                assertNotNull(empl.getManager());
            }
            else {
                assertNull(empl.getManager());
            }
            assertNotNull(empl.getNickname());
        }
    }
    
    public void testFindByFingerprint0() {
        int i = 0;
        DEmployee actual = employeeDao.findByFingerprint(String.format("%dfingerprint%d", i, i));
        LOG.info("actual={}", actual);
        assertNull(actual);
    }
    
    public void testFindByFingerprint() {
        for (int i = 0; i < 132; i++) {
            DEmployee actual = employeeDao.findByFingerprint(String.format("%dfingerprint%d", i, i));
            if (0 == i) {
                assertNull(actual);
            }
            else {
                assertNotNull(actual);
                assertEquals(String.format("%dfingerprint%d", i, i), actual.getFingerprint());
                if (1 < i) {
                    assertNotNull(actual.getManager());
                }
                else {
                    assertNull(actual.getManager());
                }
                assertNotNull(actual.getNickname());
            }
        }        
    }
    
    public void testQueryByManager() {
        
    }
}
