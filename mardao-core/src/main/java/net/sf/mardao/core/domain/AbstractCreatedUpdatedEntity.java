package net.sf.mardao.core.domain;

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
public abstract class AbstractCreatedUpdatedEntity implements Serializable {
    
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
