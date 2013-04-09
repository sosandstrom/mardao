package net.sf.mardao.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.sf.mardao.core.dao.AbstractDatabaseHelper;

/**
 * Generated Database Helper, which instantiates the entity Dao(s).
 * @author mardao
 */
public abstract class GeneratedDatabaseHelper extends AbstractDatabaseHelper {

    /** Dao for Entity ExtendsBean */
    protected final ExtendsBeanDao extendsBeanDao;
    /** Dao for Entity IdBean */
    protected final IdBeanDao idBeanDao;
    
    protected GeneratedDatabaseHelper(Context context) {
        super(context);

        // instantiate m2m Daos first

        // then ordinary Daos
        extendsBeanDao = new ExtendsBeanDaoBean();
        ((ExtendsBeanDaoBean) extendsBeanDao).setDatabaseHelper(this);
        idBeanDao = new IdBeanDaoBean();
        ((IdBeanDaoBean) idBeanDao).setDatabaseHelper(this);
    }

    @Override
    public void onCreate(SQLiteDatabase sqld) {
        Log.i(TAG, "DatabaseHelper.onCreate()");
    ((ExtendsBeanDaoBean) extendsBeanDao).onCreate(sqld);
    ((IdBeanDaoBean) idBeanDao).onCreate(sqld);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, int fromVersion, int toVersion) {
        Log.i(TAG, "DatabaseHelper.onUpgrade()");

        ((ExtendsBeanDaoBean) extendsBeanDao).onUpgrade(sqld, fromVersion, toVersion);
        ((IdBeanDaoBean) idBeanDao).onUpgrade(sqld, fromVersion, toVersion);
    }
    
}
