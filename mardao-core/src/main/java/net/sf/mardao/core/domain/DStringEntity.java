/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.core.domain;

/**
 * 
 * @author os
 */
public abstract class DStringEntity extends DCreatedUpdatedEntity<String> {

    /**
     * 
     */
    private static final long serialVersionUID = -8380169217299802124L;

    @Override
    public final Class<String> getIdClass() {
        return String.class;
    }

}
