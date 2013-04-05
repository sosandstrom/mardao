package net.sf.mardao.core.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author os
 */
@Entity
public abstract class AndroidLongEntity extends AbstractCreatedUpdatedEntity {
    
    @Id
    private Long _id;
    
    @Override
    public String subString() {
        return String.format("_id:%d", _id);
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        AndroidLongEntity ale = (AndroidLongEntity) other;
        return null == _id ? null == ale._id : _id.equals(ale._id);
    }

    public AndroidLongEntity() {
    }

    public AndroidLongEntity(Long id) {
        this._id = id;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    
}
