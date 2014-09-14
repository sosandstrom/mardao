/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.mardao.core;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author sosandstrom
 */
public class MardaoListFuture<T> implements Future<List<?>> {

    private final Future<List<?>> datastoreFuture;
    private final Iterable<T> domains;

    public MardaoListFuture(Future<List<?>> datastoreFuture, Iterable<T> domains) {
        this.datastoreFuture = datastoreFuture;
        this.domains = domains;
    }

    @Override
    public boolean cancel(boolean bln) {
        return datastoreFuture.cancel(bln);
    }

    @Override
    public boolean isCancelled() {
        return datastoreFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return datastoreFuture.isDone();
    }

    @Override
    public List<?> get() throws InterruptedException, ExecutionException {
        return datastoreFuture.get();
    }

    @Override
    public List<?> get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
        return datastoreFuture.get(l, tu);
    }

    public Iterable<T> getDomains() {
        return domains;
    }

}
