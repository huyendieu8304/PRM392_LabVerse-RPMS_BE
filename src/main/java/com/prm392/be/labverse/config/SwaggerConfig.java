package com.prm392.be.labverse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {


    //   /v3/api-docs.yaml
    //   swagger-ui.html
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OpenApi specification - LabVerse")
                        .description("OpenApi documentation for LabVerse - Research paper management system")
                        .version("1.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/")
                                .description("Local ENV")
                ));
//                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))


    }
}