package no.nav.testnav.libs.reactivesecurity.properties;


import no.nav.testnav.libs.securitycore.domain.ResourceServerType;

public class TokenxResourceServerProperties extends ResourceServerProperties {

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }

}