package no.nav.testnav.libs.standalone.reactivesecurity.properties;

import lombok.Getter;
import lombok.Setter;
import no.nav.testnav.libs.standalone.reactivesecurity.domain.ResourceServerType;

import java.util.List;

@Getter
@Setter
public abstract class ResourceServerProperties {
    private String issuerUri;
    private String jwkSetUri;
    private List<String> acceptedAudience;

    public abstract ResourceServerType getType();
}