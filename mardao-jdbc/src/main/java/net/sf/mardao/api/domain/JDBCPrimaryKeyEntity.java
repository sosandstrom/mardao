package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;

import net.sf.mardao.api.jdbc.Entity;

public abstract class JDBCPrimaryKeyEntity<ID extends Serializable> implements PrimaryKeyEntity, CreatedUpdatedEntity {
    private static final long serialVersionUID = -6242838362189491905L;

    public abstract Class<ID> getIdClass();

    public String getKind() {
        return getClass().getSimpleName();
    }

    private Long _primaryKey;

    public abstract ID getSimpleKey();

    public Long getParentKey() {
        return null;
    }

    public final Long getPrimaryKey() {
        return _primaryKey;
    }

    public final void setPrimaryKey(Long primaryKey) {
        this._primaryKey = primaryKey;
    }

    public Date getCreatedDate() {
        return null;
    }

    public Date getUpdatedDate() {
        return null;
    }

    public Entity _createEntity() {
        return new Entity(this, null, -1, null);
    }

    public String _getNameCreatedDate() {
        return null;
    }

    public String _getNameUpdatedDate() {
        return null;
    }

    public void _setCreatedDate(Date createdDate) {
    }

    public void _setUpdatedDate(Date updatedDate) {
    }

}
