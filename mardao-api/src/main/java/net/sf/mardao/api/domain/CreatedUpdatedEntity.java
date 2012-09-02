package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;

public interface CreatedUpdatedEntity<ID extends Serializable> extends PrimaryKeyEntity<ID> {

    String getCreatedBy();
    
    Date getCreatedDate();

    String getUpdatedBy();
    
    Date getUpdatedDate();

    String _getNameCreatedBy();

    String _getNameCreatedDate();

    void _setCreatedBy(String name);

    void _setCreatedDate(Date date);

    String _getNameUpdatedBy();

    String _getNameUpdatedDate();

    void _setUpdatedBy(String name);

    void _setUpdatedDate(Date date);
}
