package net.sf.mardao.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import net.sf.mardao.core.CreatedBy;
import net.sf.mardao.core.CreatedDate;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 2014-09-03 Time: 20:12
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class DUser {

  @Id
  private Long id;

  @Basic
  private String displayName;

  @Basic
  private String email;

  @CreatedDate
  @Basic
  private Date birthDate;

  @CreatedBy
  @Basic
  private String createdBy;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }
}
