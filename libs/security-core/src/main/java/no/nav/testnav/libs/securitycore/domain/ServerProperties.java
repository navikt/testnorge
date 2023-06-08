package no.nav.testnav.libs.securitycore.domain;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("no.nav.testnav")
public interface ServerProperties {

    String getCluster();

    String getNamespace();

    String getName();

    String getUrl();

    default String toTokenXScope() {
        return "%s:%s:%s".formatted(getCluster(), getNamespace(), getName());
    }

    default String toAzureAdScope() {
        return "api://%s.%s.%s/.default".formatted(getCluster(), getNamespace(), getName());
    }

    default String getScope(ResourceServerType scope) {
        return switch (scope) {
            case AZURE_AD -> toAzureAdScope();
            case TOKEN_X -> toTokenXScope();
        };
    }

}