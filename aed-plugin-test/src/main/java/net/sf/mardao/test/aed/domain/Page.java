package net.sf.mardao.test.aed.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.Parent;
import net.sf.mardao.api.domain.AEDLongEntity;

import com.google.appengine.api.datastore.Key;

@Entity
public class Page extends AEDLongEntity {
    @Parent(kind = "Chapter")
    private Key               chapter;

    @Id
    private Long              pageNumber;

    private String            body;

    /** References the book's ISBN */
    // @ManyToOne
    @Basic
    private String            book;

    private static final long serialVersionUID = 5589616677376850910L;

    @Override
    public Long getSimpleKey() {
        return pageNumber;
    }

    @Override
    public Key getParentKey() {
        return chapter;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public void setChapter(Key chapter) {
        this.chapter = chapter;
    }

    public Key getChapter() {
        return chapter;
    }

    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
        this.pageNumber = pageNumber;
    }

}
