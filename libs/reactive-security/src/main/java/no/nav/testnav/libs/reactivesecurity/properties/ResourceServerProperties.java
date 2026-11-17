package no.nav.testnav.libs.reactivesecurity.properties;

import lombok.Getter;
import lombok.Setter;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;

@Getter
@Setter
public abstract class ResourceServerProperties {
    private String issuerUri;

    public abstract ResourceServerType getType();
}