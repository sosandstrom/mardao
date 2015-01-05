package net.sf.mardao.dao;

import net.sf.mardao.core.CursorPage;

import java.io.IOException;
import java.io.Serializable;

/**
 * Core crud methods.
 * Created by sosandstrom on 2015-01-04.
 */
public interface CrudDao<T, ID extends Serializable> {

    @Crud
    ID put(T entity) throws IOException;

    @Crud
    T get(Object parentKey, ID id) throws IOException;

    @Crud
    void delete(Object parentKey, ID id) throws IOException;

    @Crud
    CursorPage<T> queryPage(Object ancestorKey, int requestedPageSize, String cursorString);
}
