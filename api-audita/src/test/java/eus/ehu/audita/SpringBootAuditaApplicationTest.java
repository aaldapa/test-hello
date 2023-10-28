package eus.ehu.audita;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eus.ehu.audita.web.AuditController;

@SpringBootTest
class SpringBootAuditaApplicationTest {

	@Autowired
	AuditController auditController;

	
	@Test
	void testController() {
		Assertions.assertThat(auditController).isNotNull();
	}
}
