
package com.sneha.jobapplicationtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/api/**")

                        // ALLOWED FRONTEND ORIGINS
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://localhost:5173"
                        )

                        // ALLOWED HTTP METHODS
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"
                        )

                        // ALLOWED REQUEST HEADERS
                        .allowedHeaders("*")

                        // HEADERS EXPOSED TO FRONTEND
                        .exposedHeaders("Location")

                        // ALLOW COOKIES/AUTHENTICATED CREDENTIALS?
                        .allowCredentials(false)

                        // CACHE PREFLIGHT RESPONSE FOR ONE HOUR
                        .maxAge(3600);
            }
        };
    }
}
