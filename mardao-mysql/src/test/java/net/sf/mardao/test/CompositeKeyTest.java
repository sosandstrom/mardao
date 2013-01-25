/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.test;

import junit.framework.TestCase;
import net.sf.mardao.core.CompositeKey;

/**
 *
 * @author os
 */
public class CompositeKeyTest extends TestCase {
    
    public CompositeKeyTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCodec() {
        final CompositeKey e1 = new CompositeKey(null, 4242L, null);
        final CompositeKey e2 = new CompositeKey(e1, null, "MiddleKey");
        final CompositeKey e3 = new CompositeKey(e2, null, null);
        
        final String keyString = CompositeKey.keyToString(e3);
        System.out.println("keyString=" + keyString);
        
        final CompositeKey a3 = CompositeKey.stringToKey(keyString);
        assertNull(a3.getId());
        assertNull(a3.getName());
        assertNotNull(a3.getParentKey());
        
        final CompositeKey a2 = a3.getParentKey();
        assertNull(a2.getId());
        assertEquals(e2.getName(), a2.getName());
        assertNotNull(a2.getParentKey());
        
        final CompositeKey a1 = a2.getParentKey();
        assertEquals(e1.getId(), a1.getId());
        assertNull(a1.getName());
        assertNull(a1.getParentKey());
    }
}
