package org.project.shopservice.services.template.services;

import java.util.Map;

public interface TemplateService {
	String renderTemplate(String templateName, Map<String,Object> contextData );
}
