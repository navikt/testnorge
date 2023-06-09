package no.nav.testnav.libs.securitycore.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@EnableConfigurationProperties
@ConfigurationProperties(prefix = "server")
@Validated
@NoArgsConstructor
@Data
public abstract class ServerProperties {

    /**
     * NAIS ingress URL for target service.
     */
    @NotBlank
    @URL
    private String url;

    /**
     * NAIS cluster for target service, e.g. <pre>dev-gcp</pre.
     */
    @NotBlank
    private String cluster;

    /**
     * NAIS defined name for target service.
     */
    @NotBlank
    private String name;

    /**
     * NAIS namespace for target service, e.g. <pre>dolly</pre>.
     */
    @NotBlank
    private String namespace;

    public String toTokenXScope() {
        return "%s:%s:%s".formatted(cluster, namespace, name);
    }

    public String toAzureAdScope() {
        return "api://%s.%s.%s/.default".formatted(cluster, namespace, name);
    }

    public String getScope(ResourceServerType scope) {
        return switch (scope) {
            case AZURE_AD -> toAzureAdScope();
            case TOKEN_X -> toTokenXScope();
        };
    }

}