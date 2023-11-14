package no.nav.testnav.libs.securitycore.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

@EnableConfigurationProperties
@Validated
@NoArgsConstructor
@Data
public class ServerProperties {

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
    private String cluster = "dev-gcp";

    /**
     * NAIS defined name for target service. Sometimes used as segment for routing.
     */
    @NotBlank
    private String name;

    /**
     * NAIS namespace for target service, e.g. <pre>dolly</pre>.
     */
    @NotBlank
    private String namespace = "dolly";

    /**
     * Requested number of threads used to run requests to this target service. Not used by all clients.
     */
    private int threads = 1;

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

    /**
     * Convenience constructor.
     *
     * @param cluster See {@link #getCluster()}, defaults to {@code dev-gcp} if {@code null}.
     * @param namespace See {@link #getNamespace()}, defaults to {@code dolly} if {@code null}.
     * @param name See {@link #getName()}.
     * @param url See {@link #getUrl()}.
     * @return New instance.
     */
    public static ServerProperties of(@Nullable String cluster, @Nullable String namespace, String name, String url) {
        var properties = new ServerProperties();
        properties.setCluster(cluster == null ? "dev-gcp" : cluster);
        properties.setNamespace(namespace == null ? "dolly" : namespace);
        properties.setName(name);
        properties.setUrl(url);
        return properties;
    }

}