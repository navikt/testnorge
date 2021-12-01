package no.nav.testnav.libs.servletsecurity.exchange;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final AzureAdTokenService azureAdTokenService;

    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return azureAdTokenService.generateToken(serverProperties);
    }
}
