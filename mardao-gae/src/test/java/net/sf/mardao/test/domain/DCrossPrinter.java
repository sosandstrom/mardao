package net.sf.mardao.test.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
@Entity
public class DCrossPrinter extends AbstractLongEntity {
    @Basic
    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
