package no.nav.testnav.libs.standalone.reactivesecurity.properties;

import lombok.Getter;
import lombok.Setter;
import no.nav.testnav.libs.standalone.reactivesecurity.domain.ResourceServerType;

@Getter
@Setter
public abstract class ResourceServerProperties {
    private String issuerUri;

    public abstract ResourceServerType getType();
}