package net.sf.mardao.junit;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

/**
 *
 * @author os
 */
public class InMemoryDataFieldMaxValueIncrementer implements DataFieldMaxValueIncrementer {
    
    long nextLongValue = 1;

    @Override
    public int nextIntValue() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long nextLongValue() throws DataAccessException {
        long returnValue;
        synchronized (this) {
            returnValue = nextLongValue;
            nextLongValue++;
        }
        return returnValue;
    }

    @Override
    public String nextStringValue() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
