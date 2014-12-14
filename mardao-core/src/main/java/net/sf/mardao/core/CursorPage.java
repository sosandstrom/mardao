package net.sf.mardao.core;

/*
 * #%L
 * mardao-core
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Collection;

/**
 *
 * @author os
 */
public class CursorPage<T extends Object> {
    
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
