package eus.ehu.audita.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.ContentCachingResponseWrapper;

import eus.ehu.audita.dto.Audit;
import eus.ehu.audita.mapper.AuditMapper;
import eus.ehu.audita.model.AuditEntity;
import eus.ehu.audita.persistence.AuditRepository;

@ExtendWith({ MockitoExtension.class, SpringExtension.class })
class AuditServiceImplTest {

	@MockBean
	AuditRepository auditRepository;

	@InjectMocks
	AuditServiceImpl auditServiceImpl;

	@Spy
	AuditMapper userMapper = Mappers.getMapper(AuditMapper.class);

	@Test
	void testCreate() throws Exception {

		// Given
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(auditRepository.save(ArgumentMatchers.any(AuditEntity.class))).thenReturn(mock(AuditEntity.class));

		// When
		AuditEntity auditResp = auditServiceImpl.create(request, new ContentCachingResponseWrapper(response));

		// then
		assertThat(auditResp).isNotNull();
	}

	@SuppressWarnings("unchecked")
	@Test
	void testSearchAllHelloPathsByDate() throws Exception {
		// Given
		AuditEntity au1 = new AuditEntity();
		au1.setRequestId("123456");
		au1.setRequestDate(LocalDateTime.now());
		au1.setRequestHeaderAcceptLanguage("es-ES");
		au1.setRequestMethod("POST");
		au1.setRequestPath("/test/hello");
		au1.setRequestBody("mensaje");
		au1.setResponseHttpCode(200);
		au1.setResponseHeaderContentType("");
		au1.setResponseBody("Kaixo mensaje");

		List<AuditEntity> lstAudits = new ArrayList<AuditEntity>(Arrays.asList(au1));
		PageRequest.of(1, 1);
		Page<AuditEntity> page = new PageImpl<AuditEntity>(lstAudits, PageRequest.of(1, 1), lstAudits.size());

		when(auditRepository.findAll(ArgumentMatchers.any(Specification.class),
				ArgumentMatchers.any(PageRequest.class))).thenReturn(page);

		// When
		Page<Audit> pageResult = auditServiceImpl.searchAllHelloPathsByDate(LocalDate.now(), LocalDate.now(), 1, 1,
				ArgumentMatchers.any(), ArgumentMatchers.any());

		// then
		assertThat(pageResult).isNotNull();
		assertThat(pageResult.getSize()).isEqualTo(1);

	}

}
