package net.sf.mardao.core;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author os
 */
public class CursorPage<T extends Object, ID extends Serializable> {
    
    /** requested page size, not acutal */
    private int requestedPageSize;
    
    /** provide this to get next page */
    private Serializable cursorKey;
    
    /** the page of items */
    private Collection<T> items;

    public Serializable getCursorKey() {
        return cursorKey;
    }

    public void setCursorKey(Serializable cursorKey) {
        this.cursorKey = cursorKey;
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

    public int getRequestedPageSize() {
        return requestedPageSize;
    }

    public void setRequestedPageSize(int requestedPageSize) {
        this.requestedPageSize = requestedPageSize;
    }

}
