package net.sf.mardao.api;

import java.io.Serializable;
import java.util.Collection;
import net.sf.mardao.api.domain.PrimaryKeyEntity;

/**
 *
 * @author os
 */
public class CursorPage<T extends PrimaryKeyEntity<ID>, ID extends Serializable> {
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
