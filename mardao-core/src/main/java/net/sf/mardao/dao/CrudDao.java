package net.sf.mardao.dao;

import net.sf.mardao.core.CursorPage;

import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;
import java.io.IOException;
import java.io.Serializable;

/**
 * Core crud methods.
 * Created by sosandstrom on 2015-01-04.
 */
public interface CrudDao<T, ID extends Serializable> {

    int count(Object parentKey);

    @CachePut
    ID put(Object parentKey, ID id, T entity) throws IOException;

    @CacheResult
    T get(Object parentKey, ID id) throws IOException;

    @CacheRemove
    void delete(Object parentKey, ID id) throws IOException;

    CursorPage<T> queryPage(Object ancestorKey, int requestedPageSize, String cursorString);
}
