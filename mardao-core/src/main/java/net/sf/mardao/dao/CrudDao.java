package net.sf.mardao.dao;

import net.sf.mardao.core.CursorPage;

import java.io.IOException;
import java.io.Serializable;

/**
 * Core crud methods.
 * Created by sosandstrom on 2015-01-04.
 */
public interface CrudDao<T, ID extends Serializable> {

    int count(Object parentKey);

    ID put(Object parentKey, ID id, T entity) throws IOException;

    T get(Object parentKey, ID id) throws IOException;

    void delete(Object parentKey, ID id) throws IOException;

    CursorPage<T> queryPage(Object ancestorKey, int requestedPageSize, String cursorString);
}
