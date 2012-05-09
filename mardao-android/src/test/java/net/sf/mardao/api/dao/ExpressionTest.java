package net.sf.mardao.api.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.mardao.api.domain.AndroidLongEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.Cursor;

@RunWith(MardaoAndroidTestRunner.class)
public class ExpressionTest {

    private AndroidDaoImpl<AndroidLongEntity> daoImpl;

    @Test
    public void testIn() throws Exception {
        Expression inFilter = daoImpl.createInFilter("test", 12);
        String expressionString = new StringBuilder().append(inFilter.getColumn()).append(inFilter.getOperation()).toString();
        assertTrue(Pattern.matches("^\\s?\\w+\\sIN\\s?\\([\\w\\?]?\\)\\s?$", expressionString));
    }

    @Test
    public void testEquals() throws Exception {
        Expression equalsFilter = daoImpl.createEqualsFilter("test", 12);
        String expressionString = new StringBuilder().append(equalsFilter.getColumn()).append(equalsFilter.getOperation())
                .toString();
        assertTrue(Pattern.matches("^\\s?\\w+\\s?=\\s?[\\w\\?]?\\s?$", expressionString));
    }

    @Before
    public void before() {
        daoImpl = new AndroidDaoImpl<AndroidLongEntity>(null, null) {

            public List<String> getColumnNames() {
                // TODO Auto-generated method stub
                return null;
            }

            public String getTableName() {
                // TODO Auto-generated method stub
                return null;
            }

            public String getPrimaryKeyColumnName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected AndroidLongEntity createDomain(final Cursor cursor) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected AndroidLongEntity createDomain(final AndroidEntity entity) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected AndroidEntity createEntity(final AndroidLongEntity domain) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected void populate(final AndroidEntity entity, final Map<String, Object> nameValuePairs) {
                // TODO Auto-generated method stub

            }
        };
    }

}
