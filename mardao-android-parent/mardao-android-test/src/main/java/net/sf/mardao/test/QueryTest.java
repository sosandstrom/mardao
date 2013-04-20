/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.mardao.test;

import java.util.ArrayList;
import net.sf.mardao.core.dao.DUniqueDao;
import net.sf.mardao.core.domain.DUnique;

/**
 *
 * @author sosandstrom
 */
public class QueryTest extends AbstractDatabaseInstrumentationTestCase {
    
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

    public void testQueryByBasic() {
        ArrayList<DUnique> actual = asList(dao.queryByMessage("Same"));
        
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("Same", actual.get(0).getMessage());
        assertEquals(actual.get(0).getMessage(), actual.get(1).getMessage());
        assertFalse(actual.get(0).getEmail().equals(actual.get(1).getEmail()));
    }
    
    public void testQueryUnique() {
        DUnique actual = dao.findByEmail("second@mardao.sf.net");
        assertNotNull(actual);
        assertEquals("second@mardao.sf.net", actual.getEmail());
        
        actual = dao.findByEmail("fourth@example.com");
        assertNull(actual);
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
