package no.nav.testnav.libs.servletsecurity.exchange;


import no.nav.testnav.libs.servletsecurity.domain.ResourceServerType;

public interface TokenService extends GenerateToken {
    ResourceServerType getType();
}
