package no.nav.testnav.libs.reactivesecurity.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResourceServerProperties {
    private String issuerUri;
    private String jwkSetUri;
    private List<String> acceptedAudience;
}

