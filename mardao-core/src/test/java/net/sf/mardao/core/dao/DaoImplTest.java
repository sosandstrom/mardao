/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.core.dao;

import junit.framework.TestCase;

/**
 *
 * @author os
 */
public class DaoImplTest extends TestCase {
    
    public DaoImplTest(String testName) {
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

    public void testEscapeCsv() {
        assertEquals("Simple", "\"Hello World\"", DaoImpl.escapeCsv("Hello World"));
        assertEquals("Medium", "\"Hello \"\"World\"", DaoImpl.escapeCsv("Hello \"World"));
    }
}
