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

  @Parent(kind="DUser")
  private Object userKey;

  @Id
  private String accessToken;

  public Object getUserKey() {
    return userKey;
  }

  public void setUserKey(Object userKey) {
    this.userKey = userKey;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
