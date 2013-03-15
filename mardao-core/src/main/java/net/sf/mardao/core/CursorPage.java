package net.sf.mardao.core;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author os
 */
public class CursorPage<T extends Object, ID extends Serializable> {
    
    /** requested page size, not acutal */
    @Deprecated
    private int requestedPageSize;
    
    /** provide this to get next page */
    private String cursorKey;
    
    /** the page of items */
    private Collection<T> items;

    /**
     * The total number of items available. Use for progress indication.
     */
    private Integer totalSize;
    
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

    @Deprecated
    public int getRequestedPageSize() {
        return requestedPageSize;
    }

    @Deprecated
    public void setRequestedPageSize(int requestedPageSize) {
        this.requestedPageSize = requestedPageSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

}
