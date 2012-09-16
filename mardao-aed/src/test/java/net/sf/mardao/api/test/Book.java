package net.sf.mardao.api.test;

import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.api.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
@Entity
public class Book extends AbstractLongEntity {
    @Basic
    private String title;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
