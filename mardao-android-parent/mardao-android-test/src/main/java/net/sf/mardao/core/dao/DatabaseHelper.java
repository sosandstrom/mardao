package net.sf.mardao.core.dao;

import android.content.Context;
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
    
}
