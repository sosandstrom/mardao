package net.sf.mardao.test.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 2014-09-14 Time: 17:58
 */
@Entity
public class DEntity {

  @Id
  private Long id;

  @Basic
  private String displayName;

  @Basic Long millis;

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
}
