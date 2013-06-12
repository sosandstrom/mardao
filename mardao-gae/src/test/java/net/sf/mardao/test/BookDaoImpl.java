package net.sf.mardao.test;

import com.google.appengine.api.datastore.DeleteContext;
import com.google.appengine.api.datastore.PostDelete;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.dao.TypeDaoImpl;

/**
 *
 * @author os
 */
public class BookDaoImpl extends TypeDaoImpl<Book, Long> {
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_APPARG0 = "appArg0";
    public static final String COLUMN_NAME_APPARG1 = "appArg1";
    
    public static final Collection<String> COLUMN_NAMES = Arrays.asList(
            COLUMN_NAME_TITLE,
            COLUMN_NAME_APPARG0,
            COLUMN_NAME_APPARG1
            );

    public BookDaoImpl() {
        super(Book.class, Long.class);
        this.memCacheAll = true;
        this.memCacheEntities = true;
    }
    
    @PostDelete(kinds = {"Book"})
    void postDeleteCallback(DeleteContext context) {
        doDeleteAuditCallback(context);
    }

    @Override
    protected Object getDomainProperty(Book domain, String name) {
        Object value = null;
        if (COLUMN_NAME_TITLE.equals(name)) {
            value = domain.getTitle();
        }
        else if (COLUMN_NAME_APPARG0.equals(name)) {
            value = domain.getAppArg0();
        } else if (COLUMN_NAME_APPARG1.equals(name)){
            value = domain.getAppArg1();
        } else {
            try {
                value = super.getDomainProperty(domain, name);
            }
            catch (IllegalArgumentException e) {
                throw e;
            }
        }
        return value;
    }
    
    public Iterable<Book> queryByTitle(String title) {
        Filter filter = createEqualsFilter(COLUMN_NAME_TITLE, title);
        
        return queryIterable(false, 0, -1, null, null, null, false, null, false, filter);
    }
    
    public Iterable<Book> queryByTitleAppArg0(String title, String appArg0) {
        Filter filter0 = createEqualsFilter(COLUMN_NAME_TITLE, title);
        Filter filter1 = createEqualsFilter(COLUMN_NAME_APPARG0, appArg0);
        
        return queryIterable(false, 0, -1, null, null, null, false, null, false, filter0, filter1);
    }
    
    @Override
    protected void setDomainProperty(Book domain, String name, Object value) {
        if (COLUMN_NAME_TITLE.equals(name)) {
            domain.setTitle(convertText(value));
        }
        else if (COLUMN_NAME_APPARG0.equals(name)) {
            domain.setAppArg0(convertText(value));
        } else if (COLUMN_NAME_APPARG1.equals(name)) {
            domain.setAppArg1(convertTextCollection((Collection)value));
        }
        else {
            super.setDomainProperty(domain, name, value);
        }
    }

    @Override
    protected void setDomainStringProperty(Book domain, String name, Map<String, String> properties) {
        final String value = properties.get(name);
        Class clazz = getColumnClass(name);
        // many-to-ones
        
        setDomainProperty(domain, name, parseProperty(value, clazz));
    }

    public Collection<String> getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public Class getColumnClass(String columnName) {
        if (COLUMN_NAME_ID.equals(columnName)) {
            return Long.class;
        }
        if (COLUMN_NAME_TITLE.equals(columnName)) {
            return String.class;
        }
        if (COLUMN_NAME_APPARG0.equals(columnName)) {
            return String.class;
        }
        if (COLUMN_NAME_APPARG1.equals(columnName)){
            return java.util.Collection.class;
        }
        if ("createdBy".equals(columnName) || "updatedBy".equals(columnName)) {
            return String.class;
        }
        if ("createdDate".equals(columnName) || "updatedDate".equals(columnName)) {
            return Date.class;
        }
        return Object.class;
    }
    
    public String getPrimaryKeyColumnName() {
        return COLUMN_NAME_ID;
    }

    @Override
    public Long getSimpleKey(Book domain) {
        return domain.getId();
    }

    @Override
    public void setSimpleKey(Book domain, Long simpleKey) {
        domain.setId(simpleKey);
    }

    @Override
    public String getCreatedDateColumnName() {
        return "createdDate";
    }

    @Override
    public String getUpdatedDateColumnName() {
        return "updatedDate";
    }

    @Override
    public void _setCreatedDate(Book domain, Date date) {
        domain.setCreatedDate(date);
    }

    @Override
    public void _setUpdatedDate(Book domain, Date date) {
        domain.setUpdatedDate(date);
    }

    @Override
    public String getCreatedByColumnName() {
        return "createdBy";
    }

    @Override
    public String getCreatedBy(Book domain) {
        return domain.getCreatedBy();
    }

    @Override
    public void _setCreatedBy(Book domain, String creator) {
        domain.setCreatedBy(creator);
    }

    @Override
    public String getUpdatedBy(Book domain) {
        return domain.getUpdatedBy();
    }

    @Override
    public String getUpdatedByColumnName() {
        return "updatedBy";
    }

    @Override
    public void _setUpdatedBy(Book domain, String updator) {
        domain.setUpdatedBy(updator);
    }
}
