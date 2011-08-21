package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;

public interface CreatedUpdatedEntity extends Serializable {
    Date getCreatedDate();

    Date getUpdatedDate();
}
