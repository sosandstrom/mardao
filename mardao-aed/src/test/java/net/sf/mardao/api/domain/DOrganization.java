package net.sf.mardao.api.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;

/**
 *
 * @author os
 */
@Entity
public class DOrganization extends AbstractLongEntity {
    @Basic
    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
