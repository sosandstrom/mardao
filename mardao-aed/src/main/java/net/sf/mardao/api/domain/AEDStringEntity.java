/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.domain;

import java.io.Serializable;

/**
 * 
 * @author os
 */
public abstract class AEDStringEntity extends AEDCreatedUpdatedEntity<String> {

    /**
     * 
     */
    private static final long serialVersionUID = -8380169217299802124L;

    @Override
    public final Class<String> getIdClass() {
        return String.class;
    }

}
