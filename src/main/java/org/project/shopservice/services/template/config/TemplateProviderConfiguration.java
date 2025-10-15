package org.project.shopservice.services.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class TemplateProviderConfiguration {
	@Bean
	public ClassLoaderTemplateResolver templateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/email/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		templateResolver.setOrder(1);
		templateResolver.setCheckExistence(true);
		return templateResolver;
	}
	@Bean
	public SpringTemplateEngine templateEngine(ClassLoaderTemplateResolver resolver){
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(resolver);
		return templateEngine;
	}
}
