package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author os
 */
public class Trans implements CreatedUpdatedEntity {

    public Date getCreatedDate() {
        return null;
    }

    public Date getUpdatedDate() {
        return null;
    }

    public String _getNameUpdatedDate() {
        return "_updated";
    }

    public void _setUpdatedDate(Date date) {
    }

    public String _getNameCreatedDate() {
        return "_created";
    }

    public void _setCreatedDate(Date date) {
    }

    public Serializable getParentKey() {
        return null;
    }

    public Serializable getPrimaryKey() {
        return null;
    }

    public Serializable getSimpleKey() {
        return null;
    }

    public Object _createEntity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
