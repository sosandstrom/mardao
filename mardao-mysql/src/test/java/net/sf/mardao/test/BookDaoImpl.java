package net.sf.mardao.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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

	/** Column name for field createdBy is "createdBy" */
	static final String COLUMN_NAME_CREATEDBY = "createdBy";
	/** Column name for field createdDate is "createdDate" */
	static final String COLUMN_NAME_CREATEDDATE = "createdDate";
	/** Column name for field updatedBy is "updatedBy" */
	static final String COLUMN_NAME_UPDATEDBY = "updatedBy";
	/** Column name for field updatedDate is "updatedDate" */
	static final String COLUMN_NAME_UPDATEDDATE = "updatedDate";
    
    public static final List<String> COLUMN_NAMES = Arrays.asList(
            COLUMN_NAME_CREATEDBY,
            COLUMN_NAME_CREATEDDATE,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_UPDATEDBY,
            COLUMN_NAME_UPDATEDDATE
            );

    public BookDaoImpl() {
        super(Book.class, Long.class);
//        this.memCacheAll = true;
//        this.memCacheEntities = true;
    }
    
    @Override
    public Class getColumnClass(String name) {
        Class clazz = null;
        if (COLUMN_NAME_ID.equals(name)) {
            clazz = Long.class;
        }
        
        // fields
        else if (COLUMN_NAME_CREATEDBY.equals(name)) {
            clazz = java.lang.String.class;
        }
        else if (COLUMN_NAME_CREATEDDATE.equals(name)) {
            clazz = java.util.Date.class;
        }
        else if (COLUMN_NAME_TITLE.equals(name)) {
            clazz = String.class;
        }
        else if (COLUMN_NAME_UPDATEDBY.equals(name)) {
            clazz = java.lang.String.class;
        }
        else if (COLUMN_NAME_UPDATEDDATE.equals(name)) {
            clazz = java.util.Date.class;
        }
        
        return clazz;
    }

    @Override
    protected List<String> getBasicColumnNames() {
        return COLUMN_NAMES;
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
    
    @Override
    protected void setDomainStringProperty(Book domain, String name, Map<String, String> properties) {
        final String value = properties.get(name);
        Class clazz = getColumnClass(name);
        // many-to-ones
        
        setDomainProperty(domain, name, parseProperty(value, clazz));
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
    public Date getCreatedDate(Book domain) {
        return null != domain ? domain.getCreatedDate() : null;
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
    public Date getUpdatedDate(Book domain) {
        return null != domain ? domain.getUpdatedDate() : null;
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
