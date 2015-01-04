package net.sf.mardao.dao;

import net.sf.mardao.core.CursorPage;

import java.io.IOException;
import java.io.Serializable;import java.lang.String;

/**
 * Created by sosandstrom on 2015-01-04.
 */
public interface CrudDao<T, ID extends Serializable> {

    @Crud
    ID put(T entity) throws IOException;

    @Crud
    CursorPage<T> queryPage(int requestedPageSize, String cursorString);
}
