/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.jsr107cache;

import java.util.Map;

/**
 *
 * @author sosandstrom
 */
public interface CacheFactory {
    Cache createCache(Map env) throws CacheException;
}
