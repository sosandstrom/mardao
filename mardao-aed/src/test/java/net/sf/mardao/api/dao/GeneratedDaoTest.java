package net.sf.mardao.api.dao;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.mardao.api.domain.DEmployee;

/**
 *
 * @author os
 */
public class GeneratedDaoTest extends TestCase {
    final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());
    
    GeneratedDEmployeeDao employeeDao;
    
    public GeneratedDaoTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("=== setUp() " + getName() + " ===");
        helper.setUp();
        
        final GeneratedDEmployeeDaoImpl employeeImpl = new GeneratedDEmployeeDaoImpl();
        this.employeeDao = employeeImpl;
        
        populate();
        System.out.println("--- setUp() " + getName() + " ---");
    }
    
    protected void populate() {
        System.out.println("--- populate() " + getName() + " ---");
        
        Map<Long, DEmployee> employees = new HashMap<Long, DEmployee>();
        for (int i = 0; i < 131; i++) {
            DEmployee employee = new DEmployee();
            employee.setFingerprint(String.format("%dfingerprint%d", i, i));
            employee.setNickname(String.format("Nick%d", i % 11));
            if (0 < i) {
                employee.setManager(employees.get(Long.valueOf(i / 10)));
            }
            employeeDao.persist(employee);
            employees.put(Long.valueOf(i), employee);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        System.out.println("--- tearDown() " + getName() + " ---");
        helper.tearDown();
        super.tearDown();
    }
    
    public void testQueryAll() {
        for (DEmployee empl : employeeDao.queryAll()) {
            
        }
    }
    
}
