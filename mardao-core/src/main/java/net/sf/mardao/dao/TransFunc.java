package net.sf.mardao.dao;

import java.io.IOException;

/**
 * Functional interface for transactional methods.
 *
 * @author osandstrom Date: 2014-09-27 Time: 20:05
 */
public interface TransFunc<R> {

  /**
   * Do your transactional statements in this method.
   * @param tx the transaction holder object
   * @return the result of the statements
   * @throws java.io.IOException can be thrown
   */
  R apply(TransactionHolder tx) throws IOException;
}
