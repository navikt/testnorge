package no.nav.testnav.libs.reactivesecurity.properties;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TokenxResourceServerProperties implements ResourceServerProperties {

    private final String issuerUri;
    private final List<String> acceptedAudience;

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }

}