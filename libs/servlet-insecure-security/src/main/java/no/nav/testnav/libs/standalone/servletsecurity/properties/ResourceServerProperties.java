package no.nav.testnav.libs.standalone.servletsecurity.properties;

import lombok.Getter;
import lombok.Setter;
import no.nav.testnav.libs.standalone.servletsecurity.domain.ResourceServerType;

import java.util.List;

@Getter
@Setter
public abstract class ResourceServerProperties {

    private String issuerUri;
    private List<String> acceptedAudience;

    public abstract ResourceServerType getType();

}