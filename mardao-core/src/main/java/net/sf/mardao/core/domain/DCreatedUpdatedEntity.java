package net.sf.mardao.core.domain;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @author os
 * 
 */
public abstract class DCreatedUpdatedEntity<ID extends Serializable> extends DPrimaryKeyEntity<ID> {
    /**      */
    private static final long  serialVersionUID  = 6731780654367241162L;

    public static final String NAME_CREATED_BY = "_creator";
    public static final String NAME_CREATED_DATE = "_created";
    public static final String NAME_UPDATED_BY = "_updator";
    public static final String NAME_UPDATED_DATE = "_updated";

    public static final Date   DATE_FLAG         = new Date(0);

    private String createdBy;
    private String updatedBy;
    
    private Date               createdDate;
    private Date               updatedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns when this Entity was created, i.e. first persisted
     * 
     * @return when this Entity was created, i.e. first persisted
     */
    @Override
    public final Date getCreatedDate() {
        return createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Returns when this Entity was last updated
     * 
     * @return when this Entity was last updated
     */
    @Override
    public final Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public final void _setCreatedBy(String name) {
        this.createdBy = name;
    }

    @Override
    public final void _setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public void _setUpdatedBy(String name) {
        this.updatedBy = name;
    }
    
    @Override
    public final void _setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * Override this method to change the name under which the created date is stored
     * 
     * @return the name under which the created date is stored
     */
    @Override
    public String _getNameCreatedDate() {
        return NAME_CREATED_DATE;
    }

    /**
     * Override this method to change the name under which the updated date is stored
     * 
     * @return the name under which the updated date is stored
     */
    @Override
    public String _getNameUpdatedDate() {
        return NAME_UPDATED_DATE;
    }

    public String _getNameCreatedBy() {
        return NAME_CREATED_BY;
    }

    public String _getNameUpdatedBy() {
        return NAME_UPDATED_BY;
    }

}
