package net.sf.mardao.test.domain;

import net.sf.mardao.core.Cached;

import javax.persistence.Entity;

/**
 * A page cached entity.
 *
 * @author mattiaslevin
 */
@Entity
@Cached(cachePages = true)
public class DPageCachedEntity extends DCachedEntity {

}
