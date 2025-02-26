package no.nav.testnav.libs.reactivesecurity.properties;

import no.nav.testnav.libs.securitycore.domain.ResourceServerType;

public class TrygdeetatenAzureAdResourceServerProperties extends ResourceServerProperties {

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

}
