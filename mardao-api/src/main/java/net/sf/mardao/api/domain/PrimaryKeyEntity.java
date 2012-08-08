package net.sf.mardao.api.domain;

import java.io.Serializable;

public interface PrimaryKeyEntity extends Serializable {
    Serializable getParentKey();
    
    Serializable getPrimaryKey();
    
    Serializable getSimpleKey();

    Object _createEntity();
}
