package net.sf.mardao.core.dao;

import net.sf.mardao.core.dao.TypeDaoImpl;
import net.sf.mardao.core.dao.AndroidEntity;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.mardao.core.domain.AndroidLongEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.Cursor;
import java.util.Collection;
import net.sf.mardao.core.Filter;

@RunWith(MardaoAndroidTestRunner.class)
public class ExpressionTest {

    private TypeDaoImpl<AndroidLongEntity, Long> daoImpl;

//    @Test
//    public void testIn() throws Exception {
//        Expression inFilter = daoImpl.createInFilter("test", 12);
//        String expressionString = new StringBuilder().append(inFilter.getColumn()).append(inFilter.getOperation()).toString();
//        assertTrue(Pattern.matches("^\\s?\\w+\\sIN\\s?\\([\\w\\?]?\\)\\s?$", expressionString));
//    }

    @Test
    public void testEquals() throws Exception {
        Filter equalsFilter = daoImpl.createEqualsFilter("test", 12);
        String expressionString = new StringBuilder().append(equalsFilter.getColumn()).append(equalsFilter.getOperation())
                .toString();
        assertTrue(Pattern.matches("^\\s?\\w+\\s?=\\s?[\\w\\?]?\\s?$", expressionString));
    }

    @Before
    public void before() {
        daoImpl = new TypeDaoImpl<AndroidLongEntity, Long>(AndroidLongEntity.class, Long.class) {

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
            protected void setDomainStringProperty(AndroidLongEntity domain, String name, Map<String, String> properties) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            public Class getColumnClass(String columnName) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            public Long getSimpleKey(AndroidLongEntity domain) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            public void setSimpleKey(AndroidLongEntity domain, Long simpleKey) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            public int deleteAll() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
    }

}
