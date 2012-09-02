package net.sf.mardao.api;

import java.io.Serializable;
import java.util.Collection;
import net.sf.mardao.api.domain.CreatedUpdatedEntity;

/**
 *
 * @author os
 */
public class CursorPage<T extends CreatedUpdatedEntity> {
    private Serializable cursorKey;
    private Collection<T> items;

    public Serializable getCursorKey() {
        return cursorKey;
    }

    public void setCursorKey(Serializable cursorKey) {
        this.cursorKey = cursorKey;
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }
    
    
}
