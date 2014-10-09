package net.sf.mardao.test.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.core.Parent;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 2014-09-13 Time: 17:24
 */
@Entity
public class DChild {

  @Parent(kind="DEntity")
  private Object parentEntityKey;

  @Id
  private String accessToken;

  public Object getParentEntityKey() {
    return parentEntityKey;
  }

  public void setParentEntityKey(Object parentEntityKey) {
    this.parentEntityKey = parentEntityKey;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
