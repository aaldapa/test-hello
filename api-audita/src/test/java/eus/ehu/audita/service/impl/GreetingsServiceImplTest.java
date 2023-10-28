package eus.ehu.audita.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eus.ehu.audita.dto.Message;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
class GreetingsServiceImplTest {


	@MockBean
	MessageSource messageSource;

	@InjectMocks
	GreetingsServiceImpl greetingsServiceImpl;

	@Test
	void testGetGreeting() {
		//Given 
		final String reqMessage = "Mi mensaje";
		
		
		//When
		when(messageSource.getMessage("saludo", new String[] { reqMessage }, LocaleContextHolder.getLocale()))
				.thenReturn("Kaixo Mi mensaje");

		Message respMessage = greetingsServiceImpl.getGreeting(new Message(reqMessage));

		//Then
		assertEquals(respMessage.getName(), "Kaixo Mi mensaje");

	}

}
