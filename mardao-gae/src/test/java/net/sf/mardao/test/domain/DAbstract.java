package net.sf.mardao.test.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author os
 */
@Entity
public abstract class DAbstract {
    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
}
