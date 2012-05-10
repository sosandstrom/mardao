/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.domain;

/**
 * 
 * @author os
 */
public abstract class AndroidLongEntity extends AndroidCreatedUpdatedEntity<Long> {

    /**
     * 
     */
    private static final long serialVersionUID = 206571318641512930L;
    
    @Override
    public final Class<Long> getIdClass() {
        return Long.class;
    }

    @Override
    public String toString() {
        return String.format("%s{_id:%d, updated:%d, %s}", getClass().getSimpleName(),
                getSimpleKey(), getUpdatedDate().getTime(), attrToString());
    }

    /** Override to add attributes to toString */
    protected String attrToString() {
        return "";
    }
}
