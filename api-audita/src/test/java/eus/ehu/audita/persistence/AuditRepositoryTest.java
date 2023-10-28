package eus.ehu.audita.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.Predicate;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;

import eus.ehu.audita.model.AuditEntity;
import eus.ehu.audita.utils.PaginationSortingUtils;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class AuditRepositoryTest {

	@Autowired
	AuditRepository auditRepository;

	@Test
	@Order(1)
	void injectedComponentsAreNotNull() {
		assertThat(auditRepository).isNotNull();
	}

	@Test
	@Order(2)
	void testSave() {
		// Given
		AuditEntity auditEntity = AuditEntity.builder().requestId(UUID.randomUUID().toString())
				.requestHeaderAcceptLanguage("eu-ES").requestDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40))
				.requestMethod("POST").requestPath("/test/hello").requestBody("Mensaje").responseHttpCode(201)
				.responseHeaderContentType("application/vnd.ehu.test.v1+json").responseBody("Kaixo mensaje").build();

		// When
		AuditEntity audit = auditRepository.save(auditEntity);

		// Then
		assertThat(audit).isNotNull();
		assertThat(audit).hasFieldOrProperty("requestId");
		assertThat(audit).hasFieldOrProperty("requestDate");
		assertThat(audit).hasFieldOrPropertyWithValue("requestMethod", "POST");
		assertThat(audit).hasFieldOrPropertyWithValue("requestPath", "/test/hello");
		assertThat(audit).hasFieldOrPropertyWithValue("requestBody", "Mensaje");
		assertThat(audit).hasFieldOrPropertyWithValue("responseHttpCode", 201);
		assertThat(audit).hasFieldOrPropertyWithValue("responseHeaderContentType", "application/vnd.ehu.test.v1+json");
		assertThat(audit).hasFieldOrPropertyWithValue("responseBody", "Kaixo mensaje");
	}

	@Test
	@Order(3)
	void testFindAllSpecificationOfTPageable() {
		// Given
		Example<AuditEntity> auditEntityExample = Example.of(AuditEntity.builder().requestPath("/test/hello").build());

		LocalDate from = LocalDate.of(2015, Month.JULY, 29);
		Specification<AuditEntity> filter = getSearchSpecifications(from, null, auditEntityExample);

		PageRequest pageable = PaginationSortingUtils.createPageRequest(AuditEntity.class, 0, 10, null, null);

		// When
		Page<AuditEntity> auditPage = auditRepository.findAll(filter, pageable);
		
		// Then
		assertThat(auditPage).isNotNull();
		assertThat(auditPage.getTotalElements()).isEqualTo(1);
	}

	private Specification<AuditEntity> getSearchSpecifications(LocalDate fromDate, LocalDate toDate,
			Example<AuditEntity> example) {
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
