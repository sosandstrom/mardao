package net.sf.mardao.api.test;

import java.util.Arrays;
import java.util.Collection;
import net.sf.mardao.api.Filter;
import net.sf.mardao.api.dao.AEDDaoImpl;

/**
 *
 * @author os
 */
public class BookDaoImpl extends AEDDaoImpl<Book, Long> {
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TITLE = "title";
    
    public static final Collection<String> COLUMN_NAMES = Arrays.asList(
            COLUMN_NAME_TITLE
            );

    public BookDaoImpl() {
        super(Book.class);
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

    public String getPrimaryKeyColumnName() {
        return COLUMN_NAME_ID;
    }

}
