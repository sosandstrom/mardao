package net.sf.mardao.test.gae.dao;

/**
 * Implementation of Business Methods related to entity Message.
 */
public class MessageDaoBean 
	extends AbstractMessageDao
		implements MessageDao 
{

	@Override
	public void deleteAll() {
		genericDao.deleteAll();
	}

}
