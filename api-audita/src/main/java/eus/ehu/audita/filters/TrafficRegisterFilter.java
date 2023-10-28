package eus.ehu.audita.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import eus.ehu.audita.service.AuditService;
import eus.ehu.audita.utils.CachedBodyHttpServletRequest;


@Component
public class TrafficRegisterFilter extends OncePerRequestFilter {
	
	@Autowired
	AuditService auditService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String requestUri = request.getRequestURI();
		if (requestUri.startsWith("/api/h2-console") || requestUri.startsWith("/api/swagger-ui")) {
			filterChain.doFilter(request, response);
			return;
		}

		CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
		ContentCachingResponseWrapper contentCachingResponse = new ContentCachingResponseWrapper(response);

		filterChain.doFilter(cachedBodyHttpServletRequest, contentCachingResponse);
			
		try {
			saveAudit(cachedBodyHttpServletRequest, contentCachingResponse);
		} catch (Exception e) {
			logger.error("Error guardando la peticion", e);
        } finally {
        	contentCachingResponse.copyBodyToResponse();
        }

	}

	private void saveAudit(HttpServletRequest request, ContentCachingResponseWrapper response) throws IOException {
		auditService.create(request, response);
    }
	

    
}
