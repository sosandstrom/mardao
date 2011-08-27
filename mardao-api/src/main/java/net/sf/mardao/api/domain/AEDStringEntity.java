/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.domain;

/**
 *
 * @author os
 */
public abstract class AEDStringEntity extends AEDCreatedUpdatedEntity<String> {

    @Override
    public final Class<String> getIdClass() {
        return String.class;
    }
    
}
