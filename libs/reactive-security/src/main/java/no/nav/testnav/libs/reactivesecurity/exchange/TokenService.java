package no.nav.testnav.libs.reactivesecurity.exchange;

import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;

public interface TokenService extends GenerateToken {
    ResourceServerType getType();
}
