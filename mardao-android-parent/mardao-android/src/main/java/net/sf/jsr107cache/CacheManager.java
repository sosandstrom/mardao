/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.jsr107cache;

/**
 *
 * @author sosandstrom
 */
public class CacheManager {
    private static CacheManager _instance = null;
    public static CacheManager getInstance() {
        if (null == _instance) {
            _instance = new CacheManager();
        }
        return _instance;
    }
    
    public CacheFactory getCacheFactory() throws CacheException {
        throw new CacheException();
    }
}
