package net.sf.mardao.test.gae.dao;

/**
 * Implementation of Business Methods related to entity Comment.
 */
public class CommentDaoBean 
	extends AbstractCommentDao
		implements CommentDao 
{

	@Override
	public void deleteAll() {
		genericDao.deleteAll();
		
	}

}
