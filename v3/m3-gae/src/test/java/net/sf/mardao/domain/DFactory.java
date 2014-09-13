package net.sf.mardao.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom Date: 2014-09-13 Time: 17:24
 */
@Entity
public class DFactory {

  @Id
  private String providerId;

  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }
}
