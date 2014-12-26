package net.sf.mardao.core;

import net.sf.mardao.dao.Mapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by sosandstrom on 2014-12-25.
 */
public class EntityFuture<T> implements Future<T> {

    private final Mapper<T, ?> mapper;
    private final Future<?> future;

    public EntityFuture(Mapper<T, ?> mapper, Future<?> future) {
        this.mapper = mapper;
        this.future = future;
    }

    @Override
    public boolean cancel(boolean b) {
        return future.cancel(b);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        final Object value = future.get();
        return mapper.fromReadValue(value);
    }

    @Override
    public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        final Object value = future.get(l, timeUnit);
        return mapper.fromReadValue(value);
    }
}
