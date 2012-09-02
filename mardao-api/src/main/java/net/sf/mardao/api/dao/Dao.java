package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.Collection;

import net.sf.mardao.api.domain.PrimaryKeyEntity;

/**
 * 
 * @author os
 * 
 * @param <T>
 *            The domain object type
 * @param <ID>
 *            The domain object's simple key type
 * @param <P>
 *            The domain object's parent key type
 * @param <C>
 *            The Core implementation key type, e.g. Key for AED
 */
public interface Dao<T extends PrimaryKeyEntity, ID extends Serializable, P extends Serializable> {
    
    Collection<String> getColumnNames();

    String getTableName();

    String getPrimaryKeyColumnName();

}
