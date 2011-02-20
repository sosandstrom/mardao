package net.sf.mardao.test.aed.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.Parent;
import net.sf.mardao.api.domain.AEDPrimaryKeyEntity;

import com.google.appengine.api.datastore.Key;

@Entity
// @Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"book", "name"})})
public class Chapter extends AEDPrimaryKeyEntity {
    private static final long serialVersionUID = -7257544633020164506L;

    @Id
    private Long              id;

    private String            name;

    /** References the book's ISBN */
    @Parent(kind = "Book")
    private Key               book;

    @Override
    public Object getSimpleKey() {
        return id;
    }

    @Override
    public Object getParentKey() {
        return book;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
