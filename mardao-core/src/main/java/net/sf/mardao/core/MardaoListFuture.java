/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.mardao.core;

/*
 * #%L
 * mardao-core
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
