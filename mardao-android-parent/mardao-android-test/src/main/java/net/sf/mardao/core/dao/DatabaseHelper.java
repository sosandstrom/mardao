package net.sf.mardao.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.Collection;
import net.sf.mardao.core.domain.DUnique;
import net.sf.mardao.core.domain.ExtendsBean;

/**
 * Empty helper for Business Logic, created by mardao
 * @author mardao
 */
public class DatabaseHelper extends GeneratedDatabaseHelper {
    
    public DatabaseHelper(Context context) {
        super(context);
    }
    
    public Long createExtendsBean(String message) {
        ExtendsBean bean = extendsBeanDao.persist(null, message);
        return bean.getId();
    }

    public ExtendsBean getExtendsBean(Long id) {
        return extendsBeanDao.findByPrimaryKey(id);
    }
    
    public Collection<Long> persistBatch(Iterable<DUnique> batch) {
        SQLiteDatabase t = beginTransaction();
        try {
            return dUniqueDao.persist(batch);
        }
        finally {
            commitTransaction(t);
        }
    }
}
