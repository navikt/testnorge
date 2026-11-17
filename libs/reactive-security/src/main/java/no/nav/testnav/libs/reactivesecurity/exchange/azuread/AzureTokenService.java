package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
public class AzureTokenService implements TokenService {

    private final GetAuthenticatedToken getAuthenticatedToken;
    private final TexasClient texasClient;

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return getAuthenticatedToken
                .call()
                .flatMap(token -> {
                    if (isNull(token) || token.getExpiresAt().isBefore(ZonedDateTime.now().toInstant().plusSeconds(120))) {
                        log.warn("AccessToken har utløpt eller utløper innen kort tid!");
                        return Mono.error(new AccessDeniedException("Access token har utløpt eller utløper innen kort tid"));
                    }
                    if (token.isClientCredentials()) {
                        return texasClient.token(TexasClient.ENTRA_ID, serverProperties.toAzureAdScope());
                    }
                    return texasClient.exchange(TexasClient.ENTRA_ID, serverProperties.toAzureAdScope(), token.getAccessTokenValue());
                });
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

    public static class Test extends AzureTokenService {

        public Test() {
            super(null, null);
        }

        @Override
        public Mono<AccessToken> exchange(ServerProperties serverProperties) {
            return Mono.empty();
        }

    }

}
