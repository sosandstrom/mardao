package net.sf.mardao.test.aed.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.UpdatedBy;
import net.sf.mardao.api.UpdatedDate;
import net.sf.mardao.api.domain.AEDPrimaryKeyEntity;

@Entity
public class Book extends AEDPrimaryKeyEntity {
    private static final long serialVersionUID = -5236891128669604072L;

    @Id
    private String            ISBN;

    private String            title;

    @UpdatedDate
    private Date              lastUpdate;

    @UpdatedBy
    private String            lastUpdatedBy;

    @Override
    public Object getSimpleKey() {
        return ISBN;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String name) {
        this.ISBN = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
}
