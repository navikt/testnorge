package no.nav.udistub.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ApplicationProperties {

    @Value("${spring.application.name:unknown}")
    private String name;

    @Value("${spring.application.version:unknown}")
    private String version;

    @Value("${spring.application.description:unknown}")
    private String description;
}
