package eus.ehu.audita.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import eus.ehu.audita.dto.Audit;
import eus.ehu.audita.dto.Message;
import eus.ehu.audita.service.AuditService;
import eus.ehu.audita.service.GreetingsService;

@WebMvcTest(AuditController.class)
class AuditControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	AuditService auditService;

	@MockBean
	GreetingsService greetingsService;

	@Test
	@DisplayName("/test/hello")
	void testHello() throws Exception {

		// Given
		String mensajeJSON = objectMapper.writeValueAsString(new Message("mensaje"));
		
		when(greetingsService.getGreeting(ArgumentMatchers.any(Message.class)))
				.thenReturn(ArgumentMatchers.any(Message.class));
		

		// When
		final ResultActions result = this.mockMvc.perform(post("/test/hello").content(mensajeJSON)
				.contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.ACCEPT_LANGUAGE, "eu, ES")
				.accept(MediaType.valueOf("application/vnd.ehu.test.v1+json")));

		// Then
		result.andDo(print()).andExpect(status().isCreated());

	}

	@Test
	@DisplayName("/test/audits")
	void TestGetAllAudits() throws Exception {

		// Given
		Audit au1 = new Audit();
		au1.setRequestId("123456");
		au1.setRequestHeaderAcceptLanguage("");
		au1.setRequestMethod("post");
		au1.setRequestPath("/test/hello");
		au1.setRequestDate(LocalDateTime.now());
		au1.setResponseHttpCode(200);
		au1.setRequestHeaderAcceptLanguage("es-ES");
		au1.setRequestBody("Mensaje");
		
		List<Audit> lstAudits = new ArrayList<Audit>(Arrays.asList(au1));
		PageRequest.of(1, 1);
		Page<Audit> page = new PageImpl<Audit>(lstAudits, PageRequest.of(1, 1), lstAudits.size());
		
		when(auditService.searchAllHelloPathsByDate(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any()))
						.thenReturn(page);
		// when
		final ResultActions result = this.mockMvc.perform(
				get("/test/audits")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.valueOf("application/vnd.ehu.test.v1+json"))
					.param("from_date", "2023-01-01"));

		// Then
		result.andDo(print()).andExpect(status().isOk());

	}

}
