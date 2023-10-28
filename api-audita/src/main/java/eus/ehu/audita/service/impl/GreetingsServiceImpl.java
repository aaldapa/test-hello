package eus.ehu.audita.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import eus.ehu.audita.dto.Message;
import eus.ehu.audita.service.GreetingsService;

@Service
public class GreetingsServiceImpl implements GreetingsService{

	private static final Logger logger = LogManager.getLogger(GreetingsServiceImpl.class);
	
	@Autowired
	private MessageSource mensajes;

	@Override
	public Message getGreeting(Message message) {
		
		logger.info("--- getGreeting ---");
		
		return Message.builder()
				.name(mensajes.getMessage("saludo", new String[] {message.getName()}, LocaleContextHolder.getLocale()))
				.build();
	}
	
	
}
