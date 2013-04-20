package net.sf.mardao.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import net.sf.mardao.core.dao.AbstractDatabaseHelper;

/**
 * Generated Database Helper, which instantiates the entity Dao(s).
 * @author mardao
 */
public abstract class GeneratedDatabaseHelper extends AbstractDatabaseHelper {

    /** Dao for Entity ExtendsBean */
    protected final ExtendsBeanDao extendsBeanDao;
    public ExtendsBeanDao getExtendsBeanDao() {
        return extendsBeanDao;
    }
    /** Dao for Entity DUnique */
    protected final DUniqueDao dUniqueDao;
    public DUniqueDao getDUniqueDao() {
        return dUniqueDao;
    }
    /** Dao for Entity IdBean */
    protected final IdBeanDao idBeanDao;
    public IdBeanDao getIdBeanDao() {
        return idBeanDao;
    }
    
    protected GeneratedDatabaseHelper(Context context) {
        super(context);

        // instantiate m2m Daos first

        // then ordinary Daos
        extendsBeanDao = new ExtendsBeanDaoBean();
        ((ExtendsBeanDaoBean) extendsBeanDao).setDatabaseHelper(this);
        dUniqueDao = new DUniqueDaoBean();
        ((DUniqueDaoBean) dUniqueDao).setDatabaseHelper(this);
        idBeanDao = new IdBeanDaoBean();
        ((IdBeanDaoBean) idBeanDao).setDatabaseHelper(this);
    }

    @Override
    public List<TypeDaoImpl<? extends Object,Long>> getDaos() {
        return Arrays.asList(
           (ExtendsBeanDaoBean) extendsBeanDao,
           (DUniqueDaoBean) dUniqueDao,
           (IdBeanDaoBean) idBeanDao
        );
    }

    
}
