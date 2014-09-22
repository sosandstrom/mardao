package net.sf.mardao.test.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 2014-09-14 Time: 17:58
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class DEntity {

  @Id
  private Long id;

  @Basic
  private String displayName;

  @Basic Long millis;

  @Basic
  private String email;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Long getMillis() {
    return millis;
  }

  public void setMillis(Long millis) {
    this.millis = millis;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
