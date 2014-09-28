package net.sf.mardao.dao;

import java.util.Date;

/**
* Holds a generic transaction, giving a strict type to work with.
*
* @author osandstrom Date: 2014-09-27 Time: 21:03
*/
public class TransactionHolder<T> {

  private final T transaction;
  private final Date date;

  TransactionHolder(final T transaction, Date date) {
    this.transaction = transaction;
    this.date = date;
  }

  public T get() {
    return transaction;
  }

  public Date getDate() {
    return date;
  }
}
