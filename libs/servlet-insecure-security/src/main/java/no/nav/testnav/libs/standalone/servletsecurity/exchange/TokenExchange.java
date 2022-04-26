package no.nav.testnav.libs.standalone.servletsecurity.exchange;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final AzureAdTokenService azureAdTokenService;

    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return azureAdTokenService.exchange(serverProperties);
    }
}
