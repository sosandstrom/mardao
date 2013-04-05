package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import java.util.Collection;
import javax.persistence.Basic;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.domain.AndroidLongEntity;

@RunWith(MardaoAndroidTestRunner.class)
public class AndroidDaoTest {

    private static final TestBean   IN  = new TestBean(1, "one");

    private static final TestBean[] OUT = new TestBean[]{new TestBean(2, "two"), new TestBean(3, "three"),
            new TestBean(4, "four"), new TestBean(5, "five"), new TestBean(6, "six"), new TestBean(7, "seven"),
            new TestBean(8, "eight"), new TestBean(9, "nine"), new TestBean(10, "ten"),};

    private TestDao                 testDao;

    // -- Tests --

    @Test
    public void testPersistEntity() {
        testDao.persist(IN);
    }

    @Test
    public void testFindAll() {
        ArrayList actual = TypeDaoImpl.asList(testDao.queryAll());
        Assert.assertEquals(Arrays.asList(OUT), actual);
    }

    @Test
    public void testFindAllKeys() {
        ArrayList actual = TypeDaoImpl.asList(testDao.queryAllKeys());
        Assert.assertEquals(Arrays.asList(new Long[]{2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,}), actual);
    }

    @Test
    public void testQueryAll() {
        Cursor cursor = (CursorIterable) testDao.queryAll();
        if (cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndex("_id");
            int testCol = cursor.getColumnIndex("test");
            do {
                TestBean expected = OUT[cursor.getPosition()];
                Assert.assertEquals(expected.get_id().longValue(), cursor.getLong(idCol));
                Assert.assertEquals(expected.test, cursor.getString(testCol));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Test
    public void testDelete() {
        testDao.delete(IN);
    }

    @Test
    public void testSerialUid() throws Exception {
        for (Field field : TestBean.class.getFields()) {
            System.out.println(field.getName());
        }
        System.out.println("---");
        for (Field field : TestBean.class.getDeclaredFields()) {
            System.out.println(field.getName());
        }
    }

    // -- Setup & Teardown

    @Before
    public void createDao() {
        testDao = new TestDao();
        TestDao.setDatabaseHelper(mockHelper);
        testDao.getDbConnection();
    }

    @After
    public void destroyDao() {
        testDao.releaseDbConnection();
    }

    // -- Test Classes --

    private static class TestBean extends AndroidLongEntity implements Serializable {

        @Basic
        String test;

        public TestBean() {
        }

        public TestBean(final long _id, final String test) {
            super(_id);
            this.test = test;
        }

        @Override
        public String subString() {
            return String.format("%s, test:%s", super.subString(), test);
        }

        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof TestBean) {
                TestBean other = (TestBean) obj;
                return super.equals(obj) && 
                        null == test ? null == other.test : test.equals(other.test);
            }
            return false;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

    }

    private static class TestDao extends TypeDaoImpl<TestBean, Long> {

        public TestDao() {
            super(TestBean.class, Long.class);
        }

        public List<String> getColumnNames() {
            return Arrays.asList(new String[]{"test"});
        }

        public String getTableName() {
            return getClass().getSimpleName();
        }

        public String getPrimaryKeyColumnName() {
            return "_id";
        }

        public Filter createEqualsFilter(String columnName, Object value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public Filter createGreaterThanOrEqualFilter(String columnName, Object value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public Filter createInFilter(String fieldName, Collection param) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public Class getColumnClass(String columnName) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public Long getSimpleKey(TestBean domain) {
            return domain.get_id();
        }

        @Override
        protected Object getDomainProperty(TestBean domain, String name) {
            if ("_id".equals(name)) {
                return domain.get_id();
            }
            if ("test".equals(name)) {
                return domain.getTest();
            }
            return super.getDomainProperty(domain, name); //To change body of generated methods, choose Tools | Templates.
        }
        
        @Override
        protected void setDomainStringProperty(TestBean domain, String name, Map<String, String> properties) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void setSimpleKey(TestBean domain, Long simpleKey) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public int deleteAll() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    // -- Boring Easymock Stuff --

    private static SQLiteDatabase         mockDatabase;
    private static AbstractDatabaseHelper mockHelper;
    private static final List<Cursor>     cursors = new ArrayList<Cursor>();

    @BeforeClass
    public static void init() {
        mockDatabase = EasyMock.createMock(SQLiteDatabase.class);
        final Capture<ContentValues> contentValuesCapture = new Capture<ContentValues>();
        EasyMock.expect(
                mockDatabase.insertOrThrow(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                        EasyMock.and(EasyMock.capture(contentValuesCapture), EasyMock.anyObject(ContentValues.class))))
                .andAnswer(new IAnswer<Long>() {
                    public Long answer() {
                        ContentValues contentValues = contentValuesCapture.getValue();

                        Assert.assertEquals(IN.get_id(), contentValues.get("_id"));
                        Assert.assertEquals(IN.test, contentValues.get("test"));

                        return (Long) contentValues.get("_id");
                    }
                }).atLeastOnce();
        EasyMock.expect(
                mockDatabase.queryWithFactory(EasyMock.anyObject(CursorFactory.class), EasyMock.anyBoolean(),
                        EasyMock.anyObject(String.class), EasyMock.anyObject(String[].class), EasyMock.anyObject(String.class),
                        EasyMock.anyObject(String[].class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                        EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andAnswer(new IAnswer<Cursor>() {
            @SuppressWarnings("unchecked")
            public Cursor answer() {
                CursorIterable<AndroidLongEntity, Long> cursor = EasyMock.createMock(CursorIterable.class);

                final AtomicInteger cursorPosition = new AtomicInteger(-1);
                final AtomicBoolean cursorClosed = new AtomicBoolean(false);

                cursor.close();
                EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
                    public Object answer() throws Throwable {
                        cursorClosed.set(true);
                        return null;
                    }
                }).once();

                EasyMock.expect(cursor.getCount()).andReturn(OUT.length).anyTimes();

                EasyMock.expect(cursor.iterator()).andReturn(new Iterator<AndroidLongEntity>() {

                    private int i = 0;

                    public boolean hasNext() {
                        return i < OUT.length;
                    }

                    public AndroidLongEntity next() {
                        return OUT[i++];
                    }

                    public void remove() {
                    }

                }).anyTimes();

                EasyMock.expect(cursor.isAfterLast()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        return cursorPosition.get() < OUT.length;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.isBeforeFirst()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        return cursorPosition.get() < 0;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.isFirst()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        return cursorPosition.get() == 0 && OUT.length > 0;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.isLast()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        return cursorPosition.get() == OUT.length - 1;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.isClosed()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        return cursorClosed.get();
                    }
                }).anyTimes();

                EasyMock.expect(cursor.moveToFirst()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        cursorPosition.set(0);
                        return OUT.length > 0;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.moveToNext()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        return cursorPosition.incrementAndGet() < OUT.length;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.moveToLast()).andAnswer(new IAnswer<Boolean>() {
                    public Boolean answer() throws Throwable {
                        cursorPosition.set(OUT.length - 1);
                        return OUT.length > 0;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.getPosition()).andAnswer(new IAnswer<Integer>() {
                    public Integer answer() throws Throwable {
                        return cursorPosition.get();
                    }
                }).anyTimes();

                EasyMock.expect(cursor.getLong(0)).andAnswer(new IAnswer<Long>() {
                    public Long answer() throws Throwable {
                        return OUT[cursorPosition.get()].get_id();
                    }
                }).anyTimes();

                EasyMock.expect(cursor.getString(1)).andAnswer(new IAnswer<String>() {
                    public String answer() throws Throwable {
                        return OUT[cursorPosition.get()].test;
                    }
                }).anyTimes();

                EasyMock.expect(cursor.getColumnIndex("_id")).andReturn(0).anyTimes();
                EasyMock.expect(cursor.getColumnIndex("test")).andReturn(1).anyTimes();

                cursors.add(cursor);
                EasyMock.replay(cursor);

                return cursor;
            }
        }).atLeastOnce();
        EasyMock.expect(
                mockDatabase.delete(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                        EasyMock.anyObject(String[].class))).andAnswer(new IAnswer<Integer>() {
            public Integer answer() throws Throwable {
                Object[] args = EasyMock.getCurrentArguments();

                Assert.assertArrayEquals(new String[]{String.valueOf(IN.get_id())}, (Object[]) args[2]);

                return 1;
            }
        }).atLeastOnce();

        final AtomicInteger count = new AtomicInteger(0);

        mockHelper = EasyMock.createMock(AbstractDatabaseHelper.class);
        EasyMock.expect(mockHelper.getDbConnection()).andAnswer(new IAnswer<SQLiteDatabase>() {
            public SQLiteDatabase answer() throws Throwable {
                count.incrementAndGet();
                return mockDatabase;
            }
        }).anyTimes();
        mockHelper.releaseDbConnection();
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                int c = count.decrementAndGet();
                if (c < 0) {
                    Assert.fail("too many closes");
                }
                return null;
            }
        }).anyTimes();

        EasyMock.replay(mockDatabase, mockHelper);
    }

    @AfterClass
    public static void after() {
        EasyMock.verify(mockDatabase, mockHelper);
        for (Cursor cursor : cursors) {
            EasyMock.verify(cursor);
        }
    }

}
