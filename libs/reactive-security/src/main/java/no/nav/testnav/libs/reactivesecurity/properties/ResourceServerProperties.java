package no.nav.testnav.libs.reactivesecurity.properties;

import no.nav.testnav.libs.securitycore.domain.ResourceServerType;

import java.util.List;

public interface ResourceServerProperties {

    String getIssuerUri();
    List<String> getAcceptedAudience();
    ResourceServerType getType();

}