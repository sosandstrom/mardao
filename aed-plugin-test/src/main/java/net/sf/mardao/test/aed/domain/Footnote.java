package net.sf.mardao.test.aed.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.Parent;
import net.sf.mardao.api.domain.AEDPrimaryKeyEntity;

import com.google.appengine.api.datastore.Key;

@Entity
public class Footnote extends AEDPrimaryKeyEntity {
    private static final long serialVersionUID = 1L;

    @Id
    private Key               key;

    private String            name;

    @Parent(kind = "Page")
    private Key               page;

    @Override
    public Object getSimpleKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getPage() {
        return page;
    }

    public void setPage(Key page) {
        this.page = page;
    }

}
