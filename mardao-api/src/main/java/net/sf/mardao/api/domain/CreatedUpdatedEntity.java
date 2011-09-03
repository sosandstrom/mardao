package net.sf.mardao.api.domain;

import java.util.Date;

public interface CreatedUpdatedEntity extends PrimaryKeyEntity {
    Date getCreatedDate();

    Date getUpdatedDate();

    String _getNameUpdatedDate();

    void _setUpdatedDate(Date date);

    String _getNameCreatedDate();

    void _setCreatedDate(Date date);
}
