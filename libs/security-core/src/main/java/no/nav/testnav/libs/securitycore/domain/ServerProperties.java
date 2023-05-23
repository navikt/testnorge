package no.nav.testnav.libs.securitycore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerProperties {
    private String url;
    private String cluster;
    private String name;
    private String namespace;

    public String toTokenXScope() {
        return cluster + ":" + namespace + ":" + name;
    }

    public String toAzureAdScope() {
        return "api://" + cluster + "." + namespace + "." + name + "/.default";
    }

    public String getScope(ResourceServerType scope) {

        if (scope == ResourceServerType.AZURE_AD) {
            return toAzureAdScope();
        }
        if (scope == ResourceServerType.TOKEN_X) {
            return toTokenXScope();
        }
        return null;
    }
}