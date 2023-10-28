package eus.ehu.audita.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import eus.ehu.audita.dto.Audit;
import eus.ehu.audita.model.AuditEntity;

class AuditMapperTest {

	@Test
	void testToEntity() {
		//Given
		Audit auditDTO = new Audit();
		auditDTO.setRequestHeaderAcceptLanguage("eu-ES");
		auditDTO.setRequestDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40));
		auditDTO.setRequestMethod("POST");
		auditDTO.setRequestPath("/test/hello");
		auditDTO.setRequestBody("Mensaje");
		auditDTO.setResponseHttpCode(201);
		auditDTO.setResponseHeaderContentType("application/vnd.ehu.test.v1+json");
		auditDTO.setResponseBody("Kaixo mensaje");
		
		//When
		AuditEntity auditEntity =  AuditMapper.INSTANCE.toEntity(auditDTO);

		//Then
		assertThat(auditEntity.getRequestHeaderAcceptLanguage()).isEqualTo("eu-ES");
		assertThat(auditEntity.getRequestDate()).isEqualTo(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40));
		assertThat(auditEntity.getRequestMethod()).isEqualTo("POST");
		assertThat(auditEntity.getRequestPath()).isEqualTo("/test/hello");
		assertThat(auditEntity.getRequestBody()).isEqualTo("Mensaje");
		assertThat(auditEntity.getResponseHttpCode()).isEqualTo(201);
		assertThat(auditEntity.getResponseHeaderContentType()).isEqualTo("application/vnd.ehu.test.v1+json");
		assertThat(auditEntity.getResponseBody()).isEqualTo("Kaixo mensaje");
		
	}

	@Test
	void testToDto() {
		//Given
		AuditEntity auditEntity = AuditEntity.builder().requestId(UUID.randomUUID().toString())
				.requestHeaderAcceptLanguage("eu-ES")
				.requestDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40))
				.requestMethod("POST")
				.requestPath("/test/hello")
				.requestBody("Mensaje")
				.responseHttpCode(201)
				.responseHeaderContentType("application/vnd.ehu.test.v1+json")
				.responseBody("Kaixo mensaje").build();
		//When
		Audit audotDto = AuditMapper.INSTANCE.toDto(auditEntity);

		//Then
		assertThat(audotDto.getRequestHeaderAcceptLanguage()).isEqualTo("eu-ES");
		assertThat(audotDto.getRequestDate()).isEqualTo(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40));
		assertThat(audotDto.getRequestMethod()).isEqualTo("POST");
		assertThat(audotDto.getRequestPath()).isEqualTo("/test/hello");
		assertThat(audotDto.getRequestBody()).isEqualTo("Mensaje");
		assertThat(audotDto.getResponseHttpCode()).isEqualTo(201);
		assertThat(audotDto.getResponseHeaderContentType()).isEqualTo("application/vnd.ehu.test.v1+json");
		assertThat(audotDto.getResponseBody()).isEqualTo("Kaixo mensaje");

	}

}
