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
 */
public interface Dao<T extends PrimaryKeyEntity<ID>, ID extends Serializable> {
    
    Collection<String> getColumnNames();

    String getParentKeyColumnName();

    String getPrimaryKeyColumnName();

    String getTableName();

    ID persist(T domain);

    Collection<ID> persist(Iterable<T> domains);
}
