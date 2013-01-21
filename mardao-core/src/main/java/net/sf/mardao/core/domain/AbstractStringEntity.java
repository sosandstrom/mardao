package net.sf.mardao.core.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author os
 */
@Entity
public abstract class AbstractStringEntity extends AbstractCreatedUpdatedEntity {
    
    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String subString() {
        return String.format("id:%s", id);
    }

    
}
