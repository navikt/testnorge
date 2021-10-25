package no.nav.testnav.libs.servletsecurity.exchange;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;


@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final AzureAdTokenService azureAdTokenService;

    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return azureAdTokenService.generateToken(serverProperties);
    }
}
