package net.sf.mardao.core;

import java.io.Serializable;

/**
 *
 * @author os
 */
public class CompositeKey implements Serializable {
    private CompositeKey parentKey;
    private Long id;
    private String name;

    public CompositeKey(CompositeKey parentKey, Long id, String name) {
        this.parentKey = parentKey;
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s:%s/%d", parentKey, name, id);
    }

    public CompositeKey getParentKey() {
        return parentKey;
    }

    public void setParentKey(CompositeKey parentKey) {
        this.parentKey = parentKey;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
