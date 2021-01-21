package no.nav.registre.testnorge.libs.analysisautoconfiguration.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApplicationProperties {
    private final String name;
    private final String cluster;
    private final String namespace;
    private final String basePackage;

    public ApplicationProperties(
            @Value("${application.name:${spring.application.name}}") String name,
            @Value("${application.cluster:${spring.application.cluster}}") String cluster,
            @Value("${application.namespace:${spring.application.namespace}}") String namespace,
            @Value("${application.basepackage:no.nav.registre}") String basePackage
    ) {
        this.name = name;
        this.cluster = cluster;
        this.namespace = namespace;
        this.basePackage = basePackage;
    }
}
