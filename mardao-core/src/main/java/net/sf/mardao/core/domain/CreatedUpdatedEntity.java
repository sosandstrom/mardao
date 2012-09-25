package net.sf.mardao.core.domain;

import java.io.Serializable;
import java.util.Date;

public interface CreatedUpdatedEntity extends Serializable {

    String getCreatedBy();
    
    Date getCreatedDate();

    String getUpdatedBy();
    
    Date getUpdatedDate();

}
