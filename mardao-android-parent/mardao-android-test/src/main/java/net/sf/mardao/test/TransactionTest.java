/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.mardao.test;

import java.util.ArrayList;
import java.util.Collection;
import net.sf.mardao.core.dao.DUniqueDao;
import net.sf.mardao.core.domain.DUnique;

/**
 *
 * @author sosandstrom
 */
public class TransactionTest extends AbstractDatabaseInstrumentationTestCase {
    
    DUniqueDao dao;

    @Override
    protected void setUp() throws Exception {
        super.setUp(); 
        dao = dbHelper.getDUniqueDao();
        
        dao.persist("first@mardao.sf.net", "Same");
        dao.persist("second@mardao.sf.net", "Same");
        dao.persist("third@mardao.sf.net", "Different");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown(); 
    }

    public void testCreate50k() {
        ArrayList<DUnique> batch = new ArrayList<DUnique>();
        for (int i = 0; i < 50000; i++) {
            DUnique d = new DUnique();
            d.setEmail(String.format("number%d@batch.com", i));
            d.setMessage("A common message");
            batch.add(d);
        }
        
        Collection<Long> actual = dbHelper.persistBatch(batch);
        assertEquals(50000, actual.size());
        for (Long id : actual) {
            assertNotNull(id);
        }
    }
    
    
    public static ArrayList asList(Iterable itrbl) {
        if (null == itrbl) {
            return null;
        }
        final ArrayList list = new ArrayList();
        for (Object o : itrbl) {
            list.add(o);
        }
        
        return list;
    }
}
