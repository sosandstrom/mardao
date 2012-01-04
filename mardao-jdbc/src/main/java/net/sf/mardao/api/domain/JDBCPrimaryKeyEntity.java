package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


public abstract class JDBCPrimaryKeyEntity<ID extends Serializable> implements PrimaryKeyEntity, CreatedUpdatedEntity {
    private static final long serialVersionUID = -6242838362189491905L;
    
    public abstract Class<ID> getIdClass();

    public abstract ID getSimpleKey();

    /** Simple key is identity to primary key for JDBC */
    public ID getPrimaryKey() {
        return getSimpleKey();
    }

    /**
     * Default implementation returns null
     * @return null
     */
    public Serializable getParentKey() {
        return null;
    }

    /** Fixed Entity type HashMap */
    @Override
    public HashMap<String, Object> _createEntity() {
        final HashMap<String, Object> entity = new HashMap<String, Object>();
        
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
