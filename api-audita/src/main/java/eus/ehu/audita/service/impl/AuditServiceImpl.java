package eus.ehu.audita.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;

import eus.ehu.audita.dto.Audit;
import eus.ehu.audita.mapper.AuditMapper;
import eus.ehu.audita.model.AuditEntity;
import eus.ehu.audita.persistence.AuditRepository;
import eus.ehu.audita.service.AuditService;
import eus.ehu.audita.utils.PaginationSortingUtils;

@Service
public class AuditServiceImpl implements AuditService {

	private static Logger logger = LogManager.getLogger(AuditServiceImpl.class);

	@Autowired
	private AuditRepository auditRepository;

	@Autowired
	AuditMapper auditMapper;

	@Override
	public AuditEntity create(HttpServletRequest request, ContentCachingResponseWrapper response) throws IOException {
		logger.info("--- create ---");

		AuditEntity auditEntity = buildAuditEntity(request, response);
		
		return auditRepository.save(auditEntity);
	}
	
	private AuditEntity buildAuditEntity(HttpServletRequest request, ContentCachingResponseWrapper response) throws IOException {
		
		String requestBody = null != request.getReader() ? request.getReader().lines().collect(Collectors.joining()) : null;
		
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        String requestPath = null != request.getRequestURI() ? request.getRequestURI().substring(request.getContextPath().length()) : null;
		
		return AuditEntity.builder()
				.requestId(UUID.randomUUID().toString())
				.requestDate(LocalDateTime.now())
				.requestHeaderAcceptLanguage(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))
				.requestMethod(request.getMethod())
				.requestPath(requestPath)
				.requestBody(requestBody)
				.responseHttpCode(response.getStatus())
				.responseHeaderContentType(response.getContentType())
				.responseBody(responseBody)
				.build();
	}

	@Override
	public Page<Audit> searchAllHelloPathsByDate(LocalDate fromDate, LocalDate toDate, int page, int pageSize, String sortBy, Boolean asc) {
		logger.info("--- searchAllHelloPathsByDate ---");

		PageRequest pageable = PaginationSortingUtils.createPageRequest(AuditEntity.class, page, pageSize, sortBy, asc);
		
		AuditEntity auditEntity = AuditEntity.builder().requestPath("/test/hello").build();
		Example<AuditEntity> auditEntityExample = Example.of(auditEntity);

		return auditRepository
				.findAll(
						getSearchSpecifications(fromDate, toDate, auditEntityExample),
						pageable)
				.map(auditMapper::toDto);
	}

	private Specification<AuditEntity> getSearchSpecifications(LocalDate fromDate, LocalDate toDate, Example<AuditEntity> example) {
		return (Specification<AuditEntity>) (root, query, builder) -> {

			final List<Predicate> predicates = new ArrayList<>();
			
			if (fromDate != null) {
				predicates.add(builder.greaterThanOrEqualTo(root.get("requestDate"), fromDate.atStartOfDay()));
			}

			if (toDate != null) {
				predicates.add(builder.lessThanOrEqualTo(root.get("requestDate"), toDate.atTime(LocalTime.MAX)));
			}
			
			predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

}
