package com.example.feedback_appointment.config;

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
                registry.addMapping("/feedback") // Разрешаваме достъп до този endpoint
                        .allowedOrigins("http://localhost:8080") // Разрешаваме заявки от твоя UI сървър
                        .allowedMethods("POST", "GET", "PUT", "DELETE") // Разрешаваме тези методи
                        .allowedHeaders("*"); // Разрешаваме всички хедъри
            }
        };
    }
}