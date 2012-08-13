package net.sf.mardao.test.aed.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.domain.AEDLongEntity;

@Entity
public class DCategory extends AEDLongEntity {

    @Id
    private Long            id;

    @Basic
    private String            title;

    @Override
    public Long getSimpleKey() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
