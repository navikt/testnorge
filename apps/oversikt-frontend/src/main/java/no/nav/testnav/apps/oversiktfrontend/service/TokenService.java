package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenExchange tokenExchange;

    public Mono<AccessToken> getMagicToken() {
        return tokenExchange.exchange(new MagicServerProperties("dev-gcp", "dolly", "team-dolly-lokal-app"));
    }

    public Mono<AccessToken> getToken(Application application) {
        return tokenExchange.exchange(application.toServerProperties());
    }

    public static class MagicServerProperties extends ServerProperties {
        public MagicServerProperties(String cluster, String namespace, String name) {
            super();
            super.setCluster(cluster);
            super.setNamespace(namespace);
            super.setName(name);
            super.setUrl("http://valid.but.not.used");
        }

    }

}
