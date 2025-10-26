package org.project.shopservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "Borys Sushkov - email",
						email = "suskovboris@gmail.com"
				),
				description = """
						OpenApi documentation for Simple Shop Service.
						
						[borya - Telegram](https://t.me/loboska1231)
						""",
				title = "OpenApi specification - Borys",
				version = "1.0"
		),
		servers = {
				@Server(
						description = "Local ENV",
						url = "http://localhost:8080"
				)
		},
		tags = {
				@Tag(name = "Orders"),
				@Tag(name = "Products"),
				@Tag(name = "Auth"),
		}
)
@SecurityScheme(
		name = "BearerSecurityScheme",
		description = """
				JWT auth description
				
				Put jwt token in this field w/o prefix 'Bearer'.
				
				To receive jwt token - see tag Auth.
				""",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)
class SwaggerConfig {
}
