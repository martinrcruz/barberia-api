package com.barberia.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "BarberiApp API",
                version = "1.0.0",
                description = "API REST para sistema de gestión integral de barberías",
                contact = @Contact(
                        name = "BarberiApp Support",
                        email = "soporte@barberiapp.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080/api", description = "Servidor de desarrollo"),
                @Server(url = "https://api.barberiapp.com/api", description = "Servidor de producción")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}

