package eus.ehu.audita.service;

import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.web.util.ContentCachingResponseWrapper;

import eus.ehu.audita.dto.Audit;
import eus.ehu.audita.model.AuditEntity;

public interface AuditService {

	AuditEntity create(HttpServletRequest request, ContentCachingResponseWrapper response) throws IOException;

	Page<Audit> searchAllHelloPathsByDate(LocalDate fromDate, LocalDate toDate, int page, int pageSize, String sortBy, Boolean asc);

}
