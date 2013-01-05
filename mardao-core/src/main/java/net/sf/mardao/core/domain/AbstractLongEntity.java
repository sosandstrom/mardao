package net.sf.mardao.core.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author os
 */
@Entity
public abstract class AbstractLongEntity extends AbstractCreatedUpdatedEntity {
    
    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String subString() {
        return String.format("id:%d", id);
    }

    
}
