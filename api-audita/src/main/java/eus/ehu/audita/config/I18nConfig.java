package eus.ehu.audita.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 	Configuracion de resource para el multi-idioma
 */
@Configuration
public class I18nConfig {


	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("i18n/messages"); // name of the resource bundle
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}

}
