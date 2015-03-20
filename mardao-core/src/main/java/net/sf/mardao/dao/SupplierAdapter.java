package net.sf.mardao.dao;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.filter.Filter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * Created by sosandstrom on 2015-02-27.
 */
public class SupplierAdapter<K, RV, WV, TR> extends AbstractSupplier<K, RV, WV, TR> {
    @Override
    protected Object getReadObject(RV value, String column) {
        return null;
    }

    @Override
    protected void setObject(WV value, String column, Object o) {

    }

    @Override
    public int count(TR tx, Mapper mapper, K ancestorKey, K simpleKey, Filter... filters) {
        return 0;
    }

    @Override
    public void deleteValue(TR tx, Mapper mapper, K key) throws IOException {

    }

    @Override
    public void deleteValues(TR tx, Mapper mapper, Collection<K> keys) throws IOException {

    }

    @Override
    public RV readValue(TR tx, Mapper mapper, K key) throws IOException {
        return null;
    }

    @Override
    public K writeValue(TR tx, Mapper mapper, K key, WV value) throws IOException {
        return null;
    }

    @Override
    public Future<RV> readFuture(TR tx, Mapper mapper, K key) throws IOException {
        return null;
    }

    @Override
    public Future<K> writeFuture(TR tx, Mapper mapper, K key, WV value) throws IOException {
        return null;
    }

    @Override
    public K toKey(K parentKey, String kind, Serializable id) {
        return null;
    }

    @Override
    public Long toLongKey(K key) {
        return null;
    }

    @Override
    public String toStringKey(K key) {
        return null;
    }

    @Override
    public K toParentKey(K key) {
        return null;
    }

    @Override
    public WV createWriteValue(Mapper mapper, K parentKey, Long id, Object entity) {
        return null;
    }

    @Override
    public WV createWriteValue(Mapper mapper, K parentKey, String id, Object entity) {
        return null;
    }

    @Override
    public TR beginTransaction() {
        return null;
    }

    @Override
    public void commitTransaction(TR transaction) {

    }

    @Override
    public void rollbackActiveTransaction(TR transaction) {

    }

    @Override
    public Iterable<RV> queryIterable(TR tx, Mapper mapper, boolean keysOnly, int offset, int limit, K ancestorKey, K simpleKey, String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, Filter... filters) {
        return null;
    }

    @Override
    public RV queryUnique(TR tx, Mapper mapper, K parentKey, Filter... filters) {
        return null;
    }

    @Override
    public CursorPage<RV> queryPage(TR tx, Mapper mapper, boolean keysOnly, int requestedPageSize, K ancestorKey, String primaryOrderBy, boolean primaryIsAscending, String secondaryOrderBy, boolean secondaryIsAscending, Collection<String> projections, String cursorString, Filter... filters) {
        return null;
    }
}
