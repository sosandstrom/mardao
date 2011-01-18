package net.sf.mardao.test.aed.dao;

import net.sf.mardao.test.aed.domain.Book;

public class UberDaoBean {
	private BookDao bookDao;
	
	public void init() {
		Book book = new Book();
		book.setTitle("Good morning midnight");
		
		bookDao.persist(book);
	}
	
	public void destroy() {
//		bookDao.deleteAll();
	}

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	public BookDao getBookDao() {
		return bookDao;
	}
	
	
}
