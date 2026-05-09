package com.shortener.url.config;

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

                registry.addMapping("/**")

                        // allow frontend
                        .allowedOriginPatterns("http://localhost:5173","https://routr-one.vercel.app/**")

                        // allowed HTTP methods
                        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")

                        // allow all headers
                        .allowedHeaders("*")

                        // allow cookies/session
                        .allowCredentials(true)

                        // cache preflight
                        .maxAge(3600);
            }
        };
    }
}