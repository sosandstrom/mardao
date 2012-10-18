package net.sf.mardao.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.dao.TypeDaoImpl;

/**
 *
 * @author os
 */
public class BookDaoImpl extends TypeDaoImpl<Book, Long> {
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TITLE = "title";
    
    public static final Collection<String> COLUMN_NAMES = Arrays.asList(
            COLUMN_NAME_TITLE
            );

    public BookDaoImpl() {
        super(Book.class, Long.class);
        this.memCacheAll = true;
        this.memCacheEntities = true;
    }

    @Override
    protected Object getDomainProperty(Book domain, String name) {
        Object value = null;
        if (COLUMN_NAME_TITLE.equals(name)) {
            value = domain.getTitle();
        }
        else {
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
    
    @Override
    protected void setDomainProperty(Book domain, String name, Object value) {
        if (COLUMN_NAME_TITLE.equals(name)) {
            domain.setTitle(convertText(value));
        }
        else {
            super.setDomainProperty(domain, name, value);
        }
    }

    public Collection<String> getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public Class getColumnClass(String columnName) {
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
