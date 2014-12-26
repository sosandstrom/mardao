package net.sf.mardao.core;

import net.sf.mardao.dao.Mapper;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by sosandstrom on 2014-12-25.
 */
public class KeyFuture<T, ID extends Serializable> implements Future<ID> {

    private final Mapper<T, ID> mapper;
    private final Future<?> future;
    private final T entity;
    private final Object value;

    public KeyFuture(Mapper<T, ID> mapper, Future<?> future, T entity, Object value) {
        this.mapper = mapper;
        this.future = future;
        this.entity = entity;
        this.value = value;
    }

    @Override
    public boolean cancel(boolean b) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public ID get() throws InterruptedException, ExecutionException {
        Object key = future.get();
        ID id = mapper.fromKey(key);
        mapper.updateEntityPostWrite(entity, key, value);
        return id;
    }

    @Override
    public ID get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        Object key = future.get(l, timeUnit);
        ID id = mapper.fromKey(key);
        mapper.updateEntityPostWrite(entity, key, value);
        return id;
    }

}
