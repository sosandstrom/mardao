package net.sf.mardao.test.aed.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.domain.AEDPrimaryKeyEntity;

@Entity
public class Book extends AEDPrimaryKeyEntity {
    @Id
    private String            ISBN;

    private String            title;

    private static final long serialVersionUID = -5236891128669604072L;

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
}
