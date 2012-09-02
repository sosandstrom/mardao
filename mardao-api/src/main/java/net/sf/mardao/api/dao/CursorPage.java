package net.sf.mardao.api.dao;

import java.util.Collection;
import net.sf.mardao.api.domain.CreatedUpdatedEntity;

/**
 *
 * @author os
 */
public class CursorPage<T extends CreatedUpdatedEntity> {
    private String cursorKey;
    private Collection<T> items;

    public String getCursorKey() {
        return cursorKey;
    }

    public void setCursorKey(String cursorKey) {
        this.cursorKey = cursorKey;
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }
    
    
}
