package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;
import net.sf.mardao.api.dao.AndroidEntity;


public abstract class AndroidPrimaryKeyEntity<ID extends Serializable> implements PrimaryKeyEntity, CreatedUpdatedEntity {
    private static final long serialVersionUID = -6242838362189491905L;

    public abstract Class<ID> getIdClass();

    public String getKind() {
        return getClass().getSimpleName();
    }

    public abstract ID getSimpleKey();

    public Long getParentKey() {
        return null;
    }

    public final ID getPrimaryKey() {
        return getSimpleKey();
    }

    public AndroidEntity _createEntity() {
        final AndroidEntity entity = new AndroidEntity();
        final ID sk = getSimpleKey();
        if (null != sk) {
            entity.setProperty("_id", sk);
        }
        return entity;
    }
    
    public Date getCreatedDate() {
        return null;
    }

    public Date getUpdatedDate() {
        return null;
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
