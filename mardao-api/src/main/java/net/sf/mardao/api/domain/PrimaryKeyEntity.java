package net.sf.mardao.api.domain;

import java.io.Serializable;

public interface PrimaryKeyEntity<ID extends Serializable> {

    Serializable getParentKey();
    
    Serializable getPrimaryKey();
    
    ID getSimpleKey();

    void setParentKey(Object parentKey);
    
    void setSimpleKey(ID simpleKey);
}
