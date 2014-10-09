package net.sf.mardao.testing.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;

import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 * To test all audit features.
 *
 * @author osandstrom Date: 2014-10-09 Time: 18:58
 */
@Entity
public class DAudited extends AbstractLongEntity {
  @Basic
  private String displayName;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
