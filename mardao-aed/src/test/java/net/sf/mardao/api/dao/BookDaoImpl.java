package net.sf.mardao.api.dao;

import java.util.Arrays;
import java.util.Collection;
import net.sf.mardao.api.domain.Book;

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
    protected void setDomainProperty(Book domain, String name, Object value) {
        if (COLUMN_NAME_TITLE.equals(name)) {
            domain.setTitle(convertText(value));
        }
    }

    public Collection<String> getColumnNames() {
        return COLUMN_NAMES;
    }

    public String getPrimaryKeyColumnName() {
        return COLUMN_NAME_ID;
    }

}
