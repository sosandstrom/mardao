/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.jsr107cache;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author sosandstrom
 */
public interface Cache {
    Object get(Object key);
    Map getAll(Collection keys) throws CacheException;
    Object put(Object key, Object value);
    void putAll(Map values);
    Object remove(Object key);
}
