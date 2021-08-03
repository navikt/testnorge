package no.nav.testnav.libs.servletcore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {
    @Value("${spring.application.name:unknown}")
    private String name;
    @Value("${spring.application.version:unknown}")
    private String version;
    @Value("${spring.application.description:unknown}")
    private String description;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }
}
