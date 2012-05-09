package net.sf.mardao.manytomany.domain;

import javax.persistence.*;
import net.sf.mardao.api.domain.AndroidLongEntity;

/**
 *
 * @author os
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"owningId", "inverseId"})})
public class AndroidManyToMany extends AndroidLongEntity {
    @Id
    private Long _id;
    
    @Basic
    private Long owningId;
    
    @Basic
    private Long inverseId;

    @Override
    public Long getSimpleKey() {
        return _id;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
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
    protected String attrToString() {
        return String.format("owningId:%d, inverseId:%d", owningId, inverseId);
    }
    
    
}
