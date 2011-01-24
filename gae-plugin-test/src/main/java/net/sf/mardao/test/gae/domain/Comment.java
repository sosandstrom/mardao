package net.sf.mardao.test.gae.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Comment implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1586479915779600904L;

    /**
	 * 
	 */
	private static long staticUID = -1586479915779600904L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

	private String text;
	
	private Key messageKey;
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setMessageKey(Key messageKey) {
		this.messageKey = messageKey;
	}

	public Key getMessageKey() {
		return messageKey;
	}
}
