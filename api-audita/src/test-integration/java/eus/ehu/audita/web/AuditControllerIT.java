package eus.ehu.audita.web;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import eus.ehu.audita.dto.Message;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class AuditControllerTestIntegration {

	final static String MESSAGE_JSON = asJsonString(new Message("mensaje"));
	final static String EXPECTED_EU_MESSAGE = "Kaixo mensaje";
	final static String EXPECTED_ES_MESSAGE = "Hola mensaje";

	@Autowired
	MockMvc mockMvc;

    @Autowired
    AuditController auditController;
	

	@Test
	@DisplayName("/test/hello with language eu-ES")
	@Order(1)
	void testHelloEu() throws Exception {

		// When
		final ResultActions result = this.mockMvc.perform(post("/test/hello").content(MESSAGE_JSON)
				.contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.ACCEPT_LANGUAGE, "eu, ES")
				.accept(MediaType.valueOf("application/vnd.ehu.test.v1+json")));

		// Then
		result.andDo(print()).andExpect(status().isCreated())
				.andExpect(content().string(containsString(EXPECTED_EU_MESSAGE)));

	}

	@Test
	@DisplayName("/test/hello without language")
	@Order(2)
	void testHelloEs() throws Exception {
		
		// When
		final ResultActions result = this.mockMvc.perform(post("/test/hello").content(MESSAGE_JSON)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.valueOf("application/vnd.ehu.test.v1+json")));

		// Then
		result.andDo(print()).andExpect(status().isCreated())
				.andExpect(content().string(containsString(EXPECTED_ES_MESSAGE)));
	}

	@Test
	@DisplayName("/test/hello with bad url (Not found)")
	@Order(3)
	void testHelloNotFound() throws Exception {

		// When
		final ResultActions result = this.mockMvc.perform(post("/test/hellos").content(MESSAGE_JSON)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.valueOf("application/vnd.ehu.test.v1+json")));

		// Then
		result.andDo(print()).andExpect(status().isNotFound())
		.andExpect(MockMvcResultMatchers.header().stringValues("Content-Type", "application/problem+json"))
		.andExpect(results -> assertTrue(
				results.getResolvedException() instanceof NoHandlerFoundException))
		.andExpect(jsonPath("$.status", Matchers.is(404)))
		.andExpect(jsonPath("$.error", Matchers.is("NOT_FOUND")))
		.andExpect(jsonPath("$.message", Matchers.is("No encontrado")))
		.andExpect(jsonPath("$.path", Matchers.containsString("/test/hellos")));
	}

	@Test
	@DisplayName("/test/hello with bad contentType (Bad Request)")
	@Order(4)
	void testHelloBadRequest() throws Exception {

		// When
		final ResultActions result = this.mockMvc.perform(post("/test/hello").content(MESSAGE_JSON)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.valueOf("application/vnd.ehu.test.v2+json")));

		// Then
		result.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.header().stringValues("Content-Type", "application/problem+json"))
				.andExpect(results -> assertTrue(
						results.getResolvedException() instanceof HttpMediaTypeNotAcceptableException))
				.andExpect(jsonPath("$.status", Matchers.is(400)))
				.andExpect(jsonPath("$.error", Matchers.is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", Matchers.is("Petición incorrecta")))
				.andExpect(jsonPath("$.path", Matchers.containsString("/test/hello")));
	}

	@Test
	@DisplayName("/test/audits")
	@Order(5)
	void testGetAllAudits() throws Exception {

		// When
		final ResultActions result = this.mockMvc.perform(get("/test/audits").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.valueOf("application/vnd.ehu.test.v1+json"))
				.param("from_date", "2023-01-01"));
		
		// Then
		result.andDo(print()).andExpect(status().isOk())
			.andExpect(jsonPath("$.content", Matchers.hasSize(3)))
			.andExpect(jsonPath("$.content[0].requestId", Matchers.notNullValue()))
			.andExpect(jsonPath("$.content[0].responseHeaderContentType", Matchers.is("application/vnd.ehu.test.v1+json")))
			.andExpect(jsonPath("$.content[0].requestHeaderAcceptLanguage", Matchers.is("eu, ES")))
			.andExpect(jsonPath("$.content[0].requestMethod", Matchers.is("POST")))
			.andExpect(jsonPath("$.content[0].requestDate", Matchers.notNullValue()))
			.andExpect(jsonPath("$.content[0].requestPath", Matchers.is("/test/hello")))
			.andExpect(jsonPath("$.content[0].responseHttpCode", Matchers.is(201)))
			.andExpect(jsonPath("$.content[0].requestBody", Matchers.containsString(MESSAGE_JSON)))
			.andExpect(jsonPath("$.content[0].responseBody", Matchers.containsString(EXPECTED_EU_MESSAGE)))
			
			.andExpect(jsonPath("$.content[1].requestId", Matchers.notNullValue()))
			.andExpect(jsonPath("$.content[1].responseHeaderContentType", Matchers.is("application/vnd.ehu.test.v1+json")))
			.andExpect(jsonPath("$.content[1].requestHeaderAcceptLanguage", Matchers.nullValue()))
			.andExpect(jsonPath("$.content[1].requestMethod", Matchers.is("POST")))
			.andExpect(jsonPath("$.content[1].requestDate", Matchers.notNullValue()))
			.andExpect(jsonPath("$.content[1].requestPath", Matchers.is("/test/hello")))
			.andExpect(jsonPath("$.content[1].responseHttpCode", Matchers.is(201)))
			.andExpect(jsonPath("$.content[1].requestBody", Matchers.containsString(MESSAGE_JSON)))
			.andExpect(jsonPath("$.content[1].responseBody", Matchers.containsString(EXPECTED_ES_MESSAGE)))
		
			.andExpect(jsonPath("$.content[2].requestId", Matchers.notNullValue()))
			.andExpect(jsonPath("$.content[2].responseHeaderContentType", Matchers.is("application/problem+json")))
			.andExpect(jsonPath("$.content[2].requestHeaderAcceptLanguage", Matchers.nullValue()))
			.andExpect(jsonPath("$.content[2].requestMethod", Matchers.is("POST")))
			.andExpect(jsonPath("$.content[2].requestDate", Matchers.notNullValue()))
			.andExpect(jsonPath("$.content[2].requestPath", Matchers.is("/test/hello")))
			.andExpect(jsonPath("$.content[2].responseHttpCode", Matchers.is(400)))
			.andExpect(jsonPath("$.content[2].requestBody", Matchers.containsString(MESSAGE_JSON)))
			.andExpect(jsonPath("$.content[2].responseBody", Matchers.containsString("BAD_REQUEST")));
	}
	
	@Test
	@DisplayName("/test/audits No content")
	@Order(6)
	void testGetAllAuditsNoContent() throws Exception {

		// When
		final ResultActions result = this.mockMvc.perform(get("/test/audits").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.valueOf("application/vnd.ehu.test.v1+json"))
				.param("to_date", "2023-01-01"));
		
		// Then
		result.andDo(print()).andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("/test/audits without parameters (Bad Request)")
	@Order(7)
	void testGetAllAuditsBadRequest() throws Exception {

		// When
		final ResultActions result = this.mockMvc.perform(get("/test/audits").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.valueOf("application/vnd.ehu.test.v1+json")));
		
		// Then
		result.andDo(print()).andExpect(status().isBadRequest());
	}

	static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
