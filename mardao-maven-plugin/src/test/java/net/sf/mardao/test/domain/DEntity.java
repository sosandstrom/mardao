package net.sf.mardao.test.domain;

/*
 * #%L
 * net.sf.mardao:mardao-maven-plugin
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
