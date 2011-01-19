package net.sf.mardao.test.aed.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Page extends AbstractPrimaryKeyEntity {
	@Id
	private Key key;
	
	private String body;
	
	/** References the book's ISBN */
	// @ManyToOne
	@Basic
	private String book;

	/** References the book's Chapter key */
	// @ManyToOne
	@Basic
	private Long chapter;

	@Override
	public Object getPrimaryKey() {
		return key;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
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

	public void setChapter(Long chapter) {
		this.chapter = chapter;
	}

	public Long getChapter() {
		return chapter;
	}

}
