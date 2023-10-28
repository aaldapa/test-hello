package eus.ehu.audita.config;

import java.util.Arrays;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuracion de Open Api con Swagger-ui para la documentacion/pruebas de los
 * endpoints de la API
 */
@Configuration
public class OpenApiConfig {

	private static final Logger logger = LogManager.getLogger(OpenApiConfig.class);

	@Autowired
	private MessageSource mensajes;

	@Bean
	public OpenAPI openAPI(ServletContext context) {
		logger.info("Starting Swagger");

		OpenAPI openApi = new OpenAPI().info(getInfo());
		
		openApi.servers(Arrays.asList(new Server().url(context.getContextPath())));

		logger.info("Started Swagger");

		return openApi;
	}

	public Info getInfo() {
		
		mensajes.getMessage("api.info.titulo", null, LocaleContextHolder.getLocale());
		
		return new Info().title(mensajes.getMessage("api.info.titulo", null, LocaleContextHolder.getLocale()))
				.description(mensajes.getMessage("api.info.descripcion", null, LocaleContextHolder.getLocale()))
				.version(mensajes.getMessage("api.info.version", null, LocaleContextHolder.getLocale()));
	}

}
