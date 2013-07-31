package net.sf.mardao.test.dao;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.geo.DLocation;
import net.sf.mardao.core.geo.Geobox;
import net.sf.mardao.test.domain.DEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class GeoDao2Test extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(GeoDao2Test.class);
    
    final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());
    
    GeneratedDEmployeeDao employeeDao;
    
    public GeoDao2Test(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LOG.info("=== setUp() " + getName() + " ===");
        helper.setUp();
        
        final GeneratedDEmployeeDaoImpl employeeImpl = new GeneratedDEmployeeDaoImpl();
        employeeImpl.setManagerDao(employeeImpl);
        this.employeeDao = employeeImpl;
        employeeImpl.setBoxBits(Arrays.asList( Geobox.BITS_17_307m, 14, Geobox.BITS_10_39km,1));
        
        LOG.info("--- setUp() " + getName() + " ---");
        System.out.println("--- setUp() " + getName() + " ---");
    }
    
    @Override
    protected void tearDown() throws Exception {
        LOG.info("--- tearDown() " + getName() + " ---");
        helper.tearDown();
        super.tearDown();
    }
        
    protected void createLocated(long id, float latitude, float longitude) {
        DLocation location = new DLocation(latitude, longitude);
        employeeDao.persist(null, id, null, Long.toString(id), null, location, null);
    }
    
    public void testFindNearest() throws InterruptedException {
        createLocated(262001L, 11.558831f, 104.91744f);
        createLocated(235001L, 11.5756035f, 104.92121f);
        createLocated(267001L, 11.545142f, 104.92628f);
        createLocated(219036L, 51.332283f, 1.4209551f); // 9932524
        createLocated(219065L, 51.945217f, 0.6378991f); // 9962893
        createLocated(219037L, 51.3897f, 0.50231534f); // 9991253
        createLocated(219034L, 51.274197f, 0.5204079f); // 9994118
        createLocated(219040L, 51.194702f, 0.27416936f); // 10013202
        createLocated(219064L, 51.60851f, 0.021824103f); // 10015043
        createLocated(219038L, 51.272713f, 0.19199584f); // 10015857
        // 10
        
//        createLocated(219065L, 51.945217f, 0.6378991f);
        createLocated(219084L, 52.547913f, 0.08832723f);
        createLocated(219068L, 51.733826f, 0.47493652f); // 9980945
        createLocated(219071L, 51.730263f, 0.4732936f);
        // 10 + 3
        
//        createLocated(219037L, 51.3897f, 0.50231534f);
//        createLocated(219034L, 51.274197f, 0.5204079f);
        createLocated(219069L, 51.554047f, 0.2473307f); // 10002211
        createLocated(219070L, 51.560806f, 0.22313647f);
        createLocated(219072L, 51.625233f, 0.042794492f);
//        createLocated(219040L, 51.194702f, 0.27416936f);
//        createLocated(219064L, 51.60851f, 0.021824103f);
        createLocated(219067L, 51.595932f, 0.02126535f); // 10015530
//        createLocated(219038L, 51.272713f, 0.19199584f);
        createLocated(219039L, 51.425484f, 0.100043304f); // 10016463
//        createLocated(L, f, f);
        // 13 + 5
        
        Thread.sleep(2L*1000L);
        
        Collection<DEmployee> first = employeeDao.findNearest(11.563538f, 104.930175f, null, true, null, true, 0, 10);
        DLocation userLocation = new DLocation(11.563538f, 104.930175f);
        for (DEmployee e : first) {
            double d = Geobox.distance(userLocation, e.getOfficeLocation());
            System.out.println(String.format("id:%d  d:%f", e.getId(), d));
        }
        
        assertEquals("Nearest first 10", 10, first.size());
        System.out.println("-- END of first page, offset 0 limit 10 --");
        
        Collection<DEmployee> second = employeeDao.findNearest(11.563538f, 104.930175f, null, true, null, true, 9, 10);
        for (DEmployee e : second) {
            double d = Geobox.distance(userLocation, e.getOfficeLocation());
            System.out.println(String.format("id:%d  d:%f", e.getId(), d));
        }
        
        assertEquals("Nearest second 10", 9, second.size());
        System.out.println("-- END of second page, offset 9 limit 10 --");
    }
}
