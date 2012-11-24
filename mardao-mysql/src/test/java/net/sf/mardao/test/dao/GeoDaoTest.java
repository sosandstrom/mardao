package net.sf.mardao.test.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import junit.framework.TestCase;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.geo.DLocation;
import net.sf.mardao.core.geo.Geobox;
import net.sf.mardao.test.domain.DEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 *
 * @author os
 */
public class GeoDaoTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(GeoDaoTest.class);
    
    GeneratedDEmployeeDao employeeDao;
    
    public GeoDaoTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LOG.info("=== setUp() " + getName() + " ===");
        
        final GeneratedDEmployeeDaoImpl employeeImpl = new GeneratedDEmployeeDaoImpl();
        employeeImpl.setManagerDao(employeeImpl);
        
        final DataSource dataSource = new SingleConnectionDataSource(
                "jdbc:h2:mem:geoDaoTest", "mardao", "jUnit", true);
        employeeImpl.setDataSource(dataSource);
        
        this.employeeDao = employeeImpl;
        
        populate();
        LOG.info("--- setUp() " + getName() + " ---");
        System.out.println("--- setUp() " + getName() + " ---");
    }
    
    protected void populate() {
        LOG.info("--- populate() " + getName() + " ---");
        
        Map<Long, DEmployee> employees = new HashMap<Long, DEmployee>();
        for (int i = 1; i < 132; i++) {
            DEmployee employee = new DEmployee();
            employee.setId(Long.valueOf(i));
            employee.setFingerprint(String.format("%dfingerprint%d", i, i));
            employee.setOfficeLocation(0 == i % 9 ? null : 
                    new DLocation(20.0f + 0.00029f * ((i%20) - 10),
                        110.0f + 0.00028f * ((i%13)-6)));
            employeeDao.persist(employee);
            employees.put(Long.valueOf(i), employee);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        LOG.info("--- tearDown() " + getName() + " ---");
        super.tearDown();
    }
    
    public void testQueryInGeobox() {
//        Serializable cursorString = null;
//        
//        CursorPage<DEmployee, Long> page = employeeDao.queryInGeobox(20f, 110f, Geobox.BITS_18_154m, 60,
//                null, false, null, false, cursorString);
//        assertEquals(60, page.getItems().size());
//        final DLocation centre = new DLocation(20f, 110f);
//        for (DEmployee actual : page.getItems()) {
//            double distance = Geobox.distance(centre, employeeDao.getGeoLocation(actual));
//            System.out.println("   distance=" + distance);
//            assertTrue("distance", distance < 308);
//        }
//        
//        page = employeeDao.queryInGeobox(20f, 110f, Geobox.BITS_18_154m, 60,
//                null, false, null, false, page.getCursorKey());
//        assertEquals(23, page.getItems().size());
//        for (DEmployee actual : page.getItems()) {
//            double distance = Geobox.distance(centre, employeeDao.getGeoLocation(actual));
//            System.out.println("   distance=" + distance);
//            assertTrue("distance", distance < 308);
//        }
    }
    
}
