package net.sf.mardao.test.domain;

import net.sf.mardao.dao.Cached;

import javax.cache.annotation.CacheDefaults;
import javax.persistence.Entity;

/**
 * A cached entity
 * @author mattiaslevin
 */
@Entity
@Cached(cachePages = true)
public class DCachedEntity extends DEntity {

}
