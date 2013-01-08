package net.sf.mardao.core.dao;

import java.util.Map;

/**
 *
 * @author os
 */
public interface CsvConverter<T> {
    
    /**
     * Convert the specified domain object into column values.
     * @param dao
     * @param columns
     * @param domain
     * @return a Map if to be written, null if to be skipped.
     */
    Map<String, Object> getCsvColumnValues(DaoImpl dao, String[] columns, T domain);
}
