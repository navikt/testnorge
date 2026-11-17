package no.nav.testnav.libs.standalone.reactivesecurity.exchange;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final TexasClient texasClient;

    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return texasClient.token(TexasClient.ENTRA_ID, serverProperties.toAzureAdScope());
    }
}
