package net.sf.mardao.api.domain;

/**
 *
 * @author os
 */
public class BasicLongEntity extends AndroidLongEntity {
    private final Long _id;
    
    public BasicLongEntity(Long id) {
        this._id = id;
    }

    @Override
    public Long getSimpleKey() {
        return _id;
    }

    
}
