package net.sf.mardao.test.aed.dao;

import java.util.List;

import net.sf.mardao.test.aed.domain.Book;
import net.sf.mardao.test.aed.domain.Chapter;
import net.sf.mardao.test.aed.domain.Footnote;
import net.sf.mardao.test.aed.domain.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Key;

public class UberDaoBean {
    static final Logger LOG  = LoggerFactory.getLogger(UberDaoBean.class);

    static final String ISBN = "ISBN-123-4569677-01";

    private BookDao     bookDao;
    private ChapterDao  chapterDao;
    private PageDao     pageDao;
    private FootnoteDao footnoteDao;

    public void setup() {
        Book book = new Book();
        book.setISBN(ISBN);
        book.setTitle("Good morning midnight");
        bookDao.persist(book);

        Chapter chapter = new Chapter();
        chapter.setId(42L);
        chapter.setBook((Key) book.getPrimaryKey());
        chapter.setName("Prologue");
        chapterDao.persist(chapter);

        Page page1 = new Page();
        page1.setBody("Lorem ipsum dolor ...");
        page1.setBook(book.getISBN());
        page1.setChapter((Key) chapter.getPrimaryKey());
        pageDao.persist(page1);
        LOG.info("persisted " + page1);

        Footnote footnote = new Footnote();
        footnote.setName("Be aware that...");
        footnote.setPage((Key) page1.getPrimaryKey());
        footnoteDao.persist(footnote);

        test();
    }

    public void test() {
        List<Book> books = bookDao.findAll();
        chapterDao.findAll();
        pageDao.findAll();
        footnoteDao.findAll();

        Book book = bookDao.findByPrimaryKey(ISBN);

        List<Chapter> chapters = chapterDao.findByBook((Key) book.getPrimaryKey());
        if (chapters.isEmpty()) {
            LOG.error("Expected chapters in book {}", ISBN);
        }
    }

    public void destroy() {
        bookDao.deleteAll();
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
