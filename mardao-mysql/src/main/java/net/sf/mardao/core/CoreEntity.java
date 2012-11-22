package net.sf.mardao.core;

import java.util.Map;

/**
 *
 * @author os
 */
public class CoreEntity {
    private CompositeKey primaryKey;
    
    private Map<String, Object> properties;

    public CompositeKey getPrimaryKey() {
        return primaryKey;
    }

    public CompositeKey getParentKey() {
        return null != primaryKey ? primaryKey.getParentKey() : null;
    }

    public void setPrimaryKey(CompositeKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Object getProperty(String name) {
        return properties.get(name);
    }
    
    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }
}
