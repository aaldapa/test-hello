package eus.ehu.audita.web;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eus.ehu.audita.dto.Audit;
import eus.ehu.audita.dto.Message;
import eus.ehu.audita.exceptions.MissingRequestParameterException;
import eus.ehu.audita.service.AuditService;
import eus.ehu.audita.service.GreetingsService;
import eus.ehu.audita.utils.Constantes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/test")
@Validated
@CrossOrigin(origins= "${cors.origins}")
public class AuditController {

	@Autowired
	AuditService auditService;
	
	@Autowired
	GreetingsService greetingsService;

	private static Logger logger = LogManager.getLogger(AuditController.class);
	
	@Operation(summary = "Hello", description = "El servicio recibe parametro name y lo devuelve con saludo")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "OK")
		})
	@PostMapping(value = "hello", produces = Constantes.RESPONSE_HEADER_CONTENT_TYPE_OK)
	public ResponseEntity<Message> hello(@RequestHeader(value="Accept-Language", required = false) String acceptLanguage, @Valid @RequestBody Message message) {
		
		logger.info("Solicitud hello");
		
		Message greetingsMessage = greetingsService.getGreeting(message);

        return new ResponseEntity<>(greetingsMessage ,HttpStatus.CREATED);
    }

	@Operation(summary = "Audits", description = "El servicio ofrece buscador de audits")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
							@ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)})
	@GetMapping(value = "audits", produces = Constantes.RESPONSE_HEADER_CONTENT_TYPE_OK)
    public ResponseEntity<Page<Audit>> audits(
    		@RequestHeader(value="Accept-Language", required = false) String acceptLanguage,
    		@Parameter(description = "Fecha desde. Formato 2023-12-31") @RequestParam(name="from_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
    		@Parameter(description = "Fecha hasta. Formato 2023-12-31") @RequestParam(name="to_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
    		@Parameter(description = "Numero de pagina") @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
    		@Parameter(description = "Numero de elementos por pagina") @RequestParam(required = false, defaultValue = Constantes.DEFAULT_PAGE_SIZE) @Min(0) int pageSize,
    		@Parameter(description = "Campo por el que se ordena. Es valido cualquier atributo de Audit (ver abajo seccion Schemas) ") @RequestParam(required = false) String sortBy, 
    		@Parameter(description = "True para ordenacion ascendente. False para descendente") @RequestParam(required = false) Boolean asc) {
		
		logger.info("Solicitud audits");
		
		if (fromDate == null && toDate == null) {
			throw new MissingRequestParameterException("error.toDateOrFromDateRequired");
		}

		Page<Audit> audits = auditService.searchAllHelloPathsByDate(fromDate, toDate, page, pageSize, sortBy, asc);

		if (audits.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(audits);
	}

}
