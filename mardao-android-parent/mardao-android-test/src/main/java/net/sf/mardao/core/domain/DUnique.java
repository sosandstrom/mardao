package net.sf.mardao.core.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class DUnique extends AndroidLongEntity {

    @Basic
    private String message;
    
    @Basic
    private String email;

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
