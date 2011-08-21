package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;

/**
 * 
 * @author os
 * 
 */
public abstract class AEDCreatedUpdatedEntity<ID extends Serializable> extends AEDPrimaryKeyEntity<ID> {
    /**      */
    private static final long  serialVersionUID  = 6731780654367241162L;

    public static final String NAME_CREATED_DATE = "_created";
    public static final String NAME_UPDATED_DATE = "_updated";

    public static final Date   DATE_FLAG         = new Date(0);

    private Date               createdDate;
    private Date               updatedDate;

    /**
     * Returns when this Entity was created, i.e. first persisted
     * 
     * @return when this Entity was created, i.e. first persisted
     */
    public final Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Returns when this Entity was last updated
     * 
     * @return when this Entity was last updated
     */
    public final Date getUpdatedDate() {
        return updatedDate;
    }

    // public final void setCreatedDate(Date createdDate) {
    // this.createdDate = createdDate;
    // }
    //
    // public final void setUpdatedDate(Date updatedDate) {
    // this.updatedDate = updatedDate;
    // }

    /**
     * Override this method to change the name under which the created date is stored
     * 
     * @return the name under which the created date is stored
     */
    public String _getNameCreatedDate() {
        return NAME_CREATED_DATE;
    }

    /**
     * Override this method to change the name under which the updated date is stored
     * 
     * @return the name under which the updated date is stored
     */
    public String _getNameUpdatedDate() {
        return NAME_UPDATED_DATE;
    }

    /**
     * Also converts createdDate and updatedDate
     */
    @Override
    public Entity _createEntity() {
        final Entity entity = super._createEntity();

        if (null != createdDate) {
            populate(entity, _getNameCreatedDate(), createdDate);
        }

        // indicate to DAO that dates are enabled:
        populate(entity, _getNameUpdatedDate(), DATE_FLAG);

        return entity;
    }
}
