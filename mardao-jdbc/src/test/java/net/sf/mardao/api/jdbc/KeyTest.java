package net.sf.mardao.api.jdbc;

import junit.framework.TestCase;

public class KeyTest extends TestCase {

    static final String  KIND        = "net.sf.mardao.Kind_Class#1";

    static final String  NAME        = ".Name/better,worse&=";

    static final Long    ID          = -3948L;

    static final String  PARENT_KIND = "net.sf.marda.Parent_Class$3";

    static final String  PARENT_NAME = "!\"'#D.Name";

    static final Key PARENT      = KeyFactory.createKey(PARENT_KIND, PARENT_NAME);

    static final Key ALMOST      = KeyFactory.createKey(PARENT, KIND, NAME.toLowerCase());

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateKeyJdbcKeyStringString() {
        Key actual = KeyFactory.createKey(PARENT, KIND, NAME);
        assertNotNull(actual);

        String expected = "/" + PARENT_KIND + "(" + PARENT_NAME + ")/" + KIND + "(" + NAME + ")";
        assertEquals(expected, actual.toString());

        expected = "ABtuZXQuc2YubWFyZGEuUGFyZW50X0NsYXNzJDMvAAohIicjRC5OYW1l.ABpuZXQuc2YubWFyZGFvLktpbmRfQ2xhc3MjMS8AFC5OYW1lL2JldHRlcix3b3JzZSY9";
        assertEquals(expected, actual.keyString());

        Key parsed = KeyFactory.parse(actual.keyString());
        assertEquals(actual, parsed);

        assertNotSame(ALMOST, actual);
    }

    public void testCreateKeyJdbcKeyStringLong() {
        Key actual = KeyFactory.createKey(PARENT, KIND, ID);
        assertNotNull(actual);

        String expected = "/" + PARENT_KIND + "(" + PARENT_NAME + ")/" + KIND + "(" + ID + ")";
        assertEquals(expected, actual.toString());

        expected = "ABtuZXQuc2YubWFyZGEuUGFyZW50X0NsYXNzJDMvAAohIicjRC5OYW1l.ABpuZXQuc2YubWFyZGFvLktpbmRfQ2xhc3MjMSz________wlA";
        assertEquals(expected, actual.keyString());

        Key parsed = KeyFactory.parse(actual.keyString());
        assertEquals(actual, parsed);

        assertNotSame(ALMOST, actual);
    }

    public void testCreateKeyStringString() {
        Key actual = KeyFactory.createKey(KIND, NAME);
        assertNotNull(actual);

        String expected = "/" + KIND + "(" + NAME + ")";
        assertEquals(expected, actual.toString());

        expected = "ABpuZXQuc2YubWFyZGFvLktpbmRfQ2xhc3MjMS8AFC5OYW1lL2JldHRlcix3b3JzZSY9";
        assertEquals(expected, actual.keyString());

        Key parsed = KeyFactory.parse(actual.keyString());
        assertEquals(actual, parsed);

        assertNotSame(ALMOST, actual);
    }

    public void testCreateKeyStringLong() {
        Key actual = KeyFactory.createKey(KIND, ID);
        assertNotNull(actual);

        String expected = "/" + KIND + "(" + ID + ")";
        assertEquals(expected, actual.toString());

        expected = "ABpuZXQuc2YubWFyZGFvLktpbmRfQ2xhc3MjMSz________wlA";
        assertEquals(expected, actual.keyString());

        Key parsed = KeyFactory.parse(actual.keyString());
        assertEquals(actual, parsed);

        assertNotSame(ALMOST, actual);
    }

}
