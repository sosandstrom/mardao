package net.sf.mardao.manytomany.domain;

import javax.persistence.*;
import net.sf.mardao.core.domain.AndroidLongEntity;

/**
 *
 * @author os
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"owningId", "inverseId"})})
public class AndroidManyToMany extends AndroidLongEntity {

    @Basic
    private Long owningId;
    
    @Basic
    private Long inverseId;

    public AndroidManyToMany() {
    }
    
    public AndroidManyToMany(Long owningId, Long inverseId) {
        this.owningId = owningId;
        this.inverseId = inverseId;
    }

    public Long getInverseId() {
        return inverseId;
    }

    public void setInverseId(Long inverseId) {
        this.inverseId = inverseId;
    }

    public Long getOwningId() {
        return owningId;
    }

    public void setOwningId(Long owningId) {
        this.owningId = owningId;
    }

    @Override
    public String subString() {
        return String.format("%s, owningId:%d, inverseId:%d", super.subString(), 
                owningId, inverseId);
    }
    
    
}
