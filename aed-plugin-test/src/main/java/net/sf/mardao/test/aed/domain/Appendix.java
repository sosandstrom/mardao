package net.sf.mardao.test.aed.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.Parent;
import net.sf.mardao.api.domain.AEDStringEntity;

import com.google.appengine.api.datastore.Key;

@Entity
public class Appendix extends AEDStringEntity {
    private static final long serialVersionUID = -7257544633020164506L;

    @Id
    private String            name;

    /** References the book's ISBN */
    @Parent(kind = "Book")
    private Key               book;

    @Override
    public String getSimpleKey() {
        return name;
    }

    @Override
    public Key getParentKey() {
        return book;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Key getBook() {
        return book;
    }

    public void setBook(Key book) {
        this.book = book;
    }

}
