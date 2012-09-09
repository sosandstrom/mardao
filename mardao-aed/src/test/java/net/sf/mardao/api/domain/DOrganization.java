package net.sf.mardao.api.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author os
 */
@Entity
public class DOrganization extends DLongEntity{
    @Id
    private Long id;
    
    @Basic
    private String name;

    @Override
    public Long getSimpleKey() {
        return id;
    }

    @Override
    public void setSimpleKey(Long simpleKey) {
        this.id = simpleKey;
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
