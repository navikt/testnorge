package no.nav.testnav.libs.servletsecurity.exchange;

import no.nav.testnav.libs.securitycore.domain.ResourceServerType;

public interface TokenService extends ExchangeToken {
    ResourceServerType getType();
}
