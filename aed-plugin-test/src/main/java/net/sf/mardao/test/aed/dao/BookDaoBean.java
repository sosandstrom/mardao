package net.sf.mardao.test.aed.dao;

/**
 * Implementation of Business Methods related to entity Book.
 */
public class BookDaoBean 
	extends AbstractBookDao
		implements BookDao 
{

	@Override
	public int deleteAll() {
		return genericDao.deleteAll();
		
	}

}
