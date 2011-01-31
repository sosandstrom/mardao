package net.sf.mardao.api.domain;

import java.io.Serializable;

public interface PrimaryKeyEntity extends Serializable {
    Object getPrimaryKey();
}
