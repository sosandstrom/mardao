package net.sf.mardao.test.gae.web;

import net.sf.mardao.test.gae.dao.MessageDao;

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

    @ResponseStatus(value=HttpStatus.OK)
    @RequestMapping(method= RequestMethod.GET)
	public String getForm(Model model) {
        LOGGER.debug("get request for Message");
        messageDao.findAll();
        return null;
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	public MessageDao getMessageDao() {
		return messageDao;
	}

}
