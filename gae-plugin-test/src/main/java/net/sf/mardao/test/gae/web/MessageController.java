package net.sf.mardao.test.gae.web;

import net.sf.mardao.test.gae.dao.CommentDao;
import net.sf.mardao.test.gae.dao.MessageDao;
import net.sf.mardao.test.gae.domain.Comment;
import net.sf.mardao.test.gae.domain.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by f94os on  Dec 3, 2010
 */
@Controller
@RequestMapping("/message")
public class MessageController {
    private static final Logger LOGGER= LoggerFactory.getLogger(MessageController.class);
    
    private MessageDao messageDao;
    
    private CommentDao commentDao;

    @ResponseStatus(value=HttpStatus.OK)
    @RequestMapping(method= RequestMethod.GET)
	public String getForm(Model model) {
        LOGGER.debug("get request for Message");
        messageDao.findAll();
        return null;
	}
    
    @ResponseStatus(value=HttpStatus.OK)
    @RequestMapping(value = "test", method= RequestMethod.GET)
    public String testMessageComment() {
    	Message message = new Message();
    	message.setText("This is a message");
    	messageDao.persist(message);
    	
    	Comment comment = new Comment();
    	comment.setText("This is a comment");
    	comment.setMessageKey(message.getKey());
    	commentDao.persist(comment);

    	commentDao.deleteAll();
    	messageDao.deleteAll();
    	
    	return null;
    }
    

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	public MessageDao getMessageDao() {
		return messageDao;
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	public CommentDao getCommentDao() {
		return commentDao;
	}

}
