package no.nav.testnav.libs.servletsecurity.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import no.nav.testnav.libs.servletsecurity.domain.ResourceServerType;

@Getter
@Setter
public abstract class ResourceServerProperties {
    private String issuerUri;
    private String jwkSetUri;
    private List<String> acceptedAudience;

    public abstract ResourceServerType getType();
}