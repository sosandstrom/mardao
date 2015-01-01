package net.sf.mardao.domain;

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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.core.CreatedBy;
import net.sf.mardao.core.CreatedDate;
import net.sf.mardao.core.UpdatedBy;
import net.sf.mardao.core.UpdatedDate;

/**
 *
 * @author os
 */
@Entity
public abstract class AbstractCreatedUpdatedEntity implements CreatedUpdatedEntity {
    
    @Basic
    @CreatedBy
    private String createdBy;
    
    @Basic
    @CreatedDate
    private Date createdDate;
    
    @Basic
    @UpdatedBy
    private String updatedBy;
    
    @Basic
    @UpdatedDate
    private Date updatedDate;

    @Override
    public String toString() {
        return String.format("%s{createdBy:%s, createdDate:%s, updatedDate:%s, %s}",
                getClass().getSimpleName(), createdBy, createdDate, updatedDate, subString());
    }
    
    public String subString() {
        return "";
    }
    
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    
}
