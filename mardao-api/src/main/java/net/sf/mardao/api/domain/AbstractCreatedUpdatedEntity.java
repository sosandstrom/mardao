package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.api.CreatedBy;
import net.sf.mardao.api.CreatedDate;
import net.sf.mardao.api.UpdatedBy;
import net.sf.mardao.api.UpdatedDate;

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
