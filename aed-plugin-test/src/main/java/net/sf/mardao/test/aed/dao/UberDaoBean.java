package net.sf.mardao.test.aed.dao;

import net.sf.mardao.test.aed.domain.Book;
import net.sf.mardao.test.aed.domain.Chapter;
import net.sf.mardao.test.aed.domain.Footnote;
import net.sf.mardao.test.aed.domain.Page;

public class UberDaoBean {
	private BookDao bookDao;
	private ChapterDao chapterDao;
	private PageDao pageDao;
	private FootnoteDao footnoteDao;
	
	public void setup() {
		Book book = new Book();
		book.setISBN("ISBN-123-4569677-01");
		book.setTitle("Good morning midnight");
		bookDao.persist(book);
		
		Chapter chapter = new Chapter();
		chapter.setBook(book.getISBN());
		chapter.setName("Prologue");
		chapterDao.persist(chapter);
		
		Page page1 = new Page();
		page1.setBody("Lorem ipsum dolor ...");
		page1.setBook(book.getISBN());
		page1.setChapter(chapter.getId());
		pageDao.persist(page1);
		
		Footnote footnote = new Footnote();
		footnote.setName("Be aware that...");
		footnote.setPage(page1.getKey());
		footnoteDao.persist(footnote);
	}

	public void test() {
	//		bookDao.deleteAll();
		}

	public ChapterDao getChapterDao() {
		return chapterDao;
	}

	public void setChapterDao(ChapterDao chapterDao) {
		this.chapterDao = chapterDao;
	}

	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	public BookDao getBookDao() {
		return bookDao;
	}

	public FootnoteDao getFootnoteDao() {
		return footnoteDao;
	}

	public void setFootnoteDao(FootnoteDao footnoteDao) {
		this.footnoteDao = footnoteDao;
	}
	
	
}
