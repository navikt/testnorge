package no.nav.testnav.libs.securitycore.domain;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import static java.lang.String.join;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerProperties {

    private String url;
    private String cluster;
    private String name;
    private String namespace;

    @PostConstruct
    void postConstruct() {
        validate();
    }

    public String toTokenXScope() {
        validate();
        return cluster + ":" + namespace + ":" + name;
    }

    public String toAzureAdScope() {
        validate();
        return "api://" + cluster + "." + namespace + "." + name + "/.default";
    }

    void validate() {
        var errors = new ArrayList<String>(4);
        if (isBlank(url)) {
            errors.add("URL");
        }
        if (isBlank(cluster)) {
            errors.add("Cluster");
        }
        if (isBlank(name)) {
            errors.add("Name");
        }
        if (isBlank(namespace)) {
            errors.add("Namespace");
        }
        if (!errors.isEmpty()) {
            throw new IllegalStateException(join(",", errors) + " is not set; check configuration for " + this.getClass().getSimpleName());
        }
    }

    public String getScope(ResourceServerType scope) {
        return switch (scope) {
            case AZURE_AD -> toAzureAdScope();
            case TOKEN_X -> toTokenXScope();
        };
    }

}