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

        Book book2 = new Book();
        book2.setISBN("73-9482-49");
        book2.setTitle("Animal Farm");
        bookDao.persist(book2);

        Chapter prologue = new Chapter();
        prologue.setId(42L);
        prologue.setBook((Key) book.getPrimaryKey());
        prologue.setName("Prologue");
        chapterDao.persist(prologue);

        Chapter intermezzo = new Chapter();
        intermezzo.setId(73L);
        intermezzo.setBook((Key) book.getPrimaryKey());
        intermezzo.setName("Intermezzo");
        chapterDao.persist(intermezzo);

        Page page1 = new Page();
        page1.setBody("Lorem ipsum dolor ...");
        page1.setBook(book.getISBN());
        page1.setChapter((Key) prologue.getPrimaryKey());
        pageDao.persist(page1);
        LOG.info("persisted {}", page1);

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
        if (false == book.getUpdatedDate().equals(book.getCreatedDate())) {
            LOG.error("Expected updatedDate {} to be equal to createdDate {}", book.getUpdatedDate(), book.getCreatedDate());
        }

        List<Chapter> chapters = chapterDao.findByBook((Key) book.getPrimaryKey());
        if (chapters.isEmpty()) {
            LOG.error("Expected chapters in book {}", ISBN);
        }

        book.setTitle("Updated book title");
        bookDao.update(book);
        if (book.getUpdatedDate().compareTo(book.getCreatedDate()) <= 0) {
            LOG.error("Expected updatedDate {} to be after createdDate {}", book.getUpdatedDate(), book.getCreatedDate());
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
