/*
 * INSERT COPYRIGHT HERE
 */

package net.sf.mardao.test;

import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;
import android.util.Log;
import net.sf.mardao.core.dao.DatabaseHelper;

/**
 *
 * @author sosandstrom
 */
public class AbstractDatabaseInstrumentationTestCase extends InstrumentationTestCase {

    protected DatabaseHelper dbHelper;
    
    

    @Override
    protected void setUp() throws Exception {
        super.setUp(); 
        dbHelper = new DatabaseHelper(getInstrumentation().getTargetContext());
        dbHelper.dropAll();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        Log.i(getName(), "---------------- setUp() ------------------------------");
    }

    @Override
    protected void tearDown() throws Exception {
        Log.i(getName(), "---------------- tearDown() ------------------------------");
//        File dbFile = new File(dbHelper.getReadableDatabase().getPath());
//        if (dbHelper.getWritableDatabase().isOpen()) {
//            dbHelper.close();
//        }
//        
//        if (dbFile.exists()) {
//            Thread.sleep(1000L);
//            dbFile.delete();
//        }
//        
        super.tearDown(); 
    }
    
}
