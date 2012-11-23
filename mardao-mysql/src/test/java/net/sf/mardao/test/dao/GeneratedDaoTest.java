package net.sf.mardao.test.dao;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import junit.framework.TestCase;
import net.sf.mardao.test.dao.GeneratedDEmployeeDao;
import net.sf.mardao.test.dao.GeneratedDEmployeeDaoImpl;
import net.sf.mardao.test.domain.DEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 *
 * @author os
 */
public class GeneratedDaoTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(GeneratedDaoTest.class);
    
    GeneratedDEmployeeDao employeeDao;
    
    public GeneratedDaoTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LOG.info("=== setUp() " + getName() + " ===");
//        helper.setUp();
        
        final GeneratedDEmployeeDaoImpl employeeImpl = new GeneratedDEmployeeDaoImpl();
        employeeImpl.setManagerDao(employeeImpl);
        this.employeeDao = employeeImpl;
        final DataSource dataSource = new SingleConnectionDataSource(
                "jdbc:h2:mem:typeDaoTest", "mardao", "jUnit", true);
        employeeImpl.setDataSource(dataSource);
        
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
        final Iterable<Long> ids = employeeDao.queryAllKeys();
        employeeDao.delete(null, ids);
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
    
    public void testQueryByManyToOne() {
        final DEmployee manager = employeeDao.findByPrimaryKey(1L);
        assertNotNull("ManyToOne manager", manager);
        final Iterable<DEmployee> i = employeeDao.queryByManager(manager);
        final Map<Long, DEmployee> actual = new HashMap<Long, DEmployee>();
        for (DEmployee e : i) {
            actual.put(e.getId(), e);
        }
        assertEquals("ManyToOne employees", 8, actual.size());
    }
}
