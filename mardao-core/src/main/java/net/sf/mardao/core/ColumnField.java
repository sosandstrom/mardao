package net.sf.mardao.core;

/**
 * Created by sopheakboth83 on 3/15/15.
 */
public class ColumnField {
    private final String columnName;
    private final Class clazz;
    private final Class annotation;

    public ColumnField(String columnName, Class clazz, Class annotation) {
        this.columnName = columnName;
        this.clazz = clazz;
        this.annotation = annotation;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class getClazz() {
        return clazz;
    }

    public Class getAnnotation() {
        return annotation;
    }
}
