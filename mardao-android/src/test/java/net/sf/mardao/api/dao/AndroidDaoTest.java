package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.mardao.api.domain.AndroidLongEntity;

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
        testDao.persistEntity(testDao.createEntity(IN));
    }

    @Test
    public void testFindAll() {
        Assert.assertEquals(Arrays.asList(OUT), testDao.findAll());
    }

    @Test
    public void testFindAllKeys() {
        Assert.assertEquals(Arrays.asList(new Long[]{2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,}), testDao.findAllKeys());
    }

    @Test
    public void testQueryAll() {
        Cursor cursor = testDao.queryAll();
        if (cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndex("_id");
            int testCol = cursor.getColumnIndex("test");
            do {
                TestBean expected = OUT[cursor.getPosition()];
                Assert.assertEquals(expected._id, cursor.getLong(idCol));
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
        testDao = new TestDao(TestBean.class, mockHelper);
        testDao.getDbConnection();
    }

    @After
    public void destroyDao() {
        testDao.releaseDbConnection();
    }

    // -- Test Classes --

    private static class TestBean extends AndroidLongEntity implements Serializable {

        long   _id;
        String test;

        public TestBean() {
        }

        public TestBean(final int _id, final String test) {
            this._id = _id;
            this.test = test;
        }

        @Override
        public Long getSimpleKey() {
            return _id;
        }

        @Override
        public String toString() {
            return _id + " - " + test;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof TestBean) {
                TestBean other = (TestBean) obj;
                return _id == other._id && String.valueOf(test).equals(other.test);
            }
            return false;
        }

    }

    private static class TestDao extends AndroidDaoImpl<TestBean> {

        public TestDao(final Class<TestBean> type, final AbstractDatabaseHelper helper) {
            super(type, helper);
        }

        public List<String> getColumnNames() {
            return Arrays.asList(new String[]{"_id", "test"});
        }

        public String getTableName() {
            return getClass().getSimpleName();
        }

        public String getPrimaryKeyColumnName() {
            return "_id";
        }

        @Override
        protected TestBean createDomain(final Cursor cursor) {
            TestBean testBean = new TestBean();

            testBean._id = cursor.getLong(cursor.getColumnIndex("_id"));
            testBean.test = cursor.getString(cursor.getColumnIndex("test"));

            return testBean;
        }

        @Override
        protected TestBean createDomain(final AndroidEntity entity) {
            TestBean testBean = new TestBean();

            testBean._id = (Long) entity.getProperty("_id");
            testBean.test = (String) entity.getProperty("test");

            return testBean;
        }

        @Override
        protected AndroidEntity createEntity(final TestBean domain) {
            AndroidEntity entity = new AndroidEntity();

            entity.setProperty("_id", domain._id);
            entity.setProperty("test", domain.test);

            return entity;
        }

        @Override
        protected void populate(final AndroidEntity entity, final Map<String, Object> nameValuePairs) {
            throw new RuntimeException(entity + "  " + nameValuePairs);
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

                        Assert.assertEquals(IN._id, contentValues.get("_id"));
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
                CursorIterable<AndroidLongEntity> cursor = EasyMock.createMock(CursorIterable.class);

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
                        return OUT[cursorPosition.get()]._id;
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

                Assert.assertArrayEquals(new String[]{String.valueOf(IN._id)}, (Object[]) args[2]);

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
