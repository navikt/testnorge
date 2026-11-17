package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenExchangeTest {

    private final ClientRegistrationIdResolver clientRegistrationIdResolver = mock(ClientRegistrationIdResolver.class);
    private final TokenResolver tokenResolver = mock(TokenResolver.class);
    private final TexasClient texasClient = mock(TexasClient.class);
    private final ServerWebExchange exchange = mock(ServerWebExchange.class);
    private final ServerProperties serverProperties = new ServerProperties();

    private TokenExchange tokenExchange;

    @BeforeEach
    void setUp() {
        serverProperties.setName("testnav-app");
        serverProperties.setNamespace("dolly");
        serverProperties.setCluster("dev-gcp");
        serverProperties.setUrl("http://testnav-app.local");
        when(tokenResolver.getToken(exchange)).thenReturn(Mono.just(Token.builder().accessTokenValue("session-token").build()));
        tokenExchange = new TokenExchange(clientRegistrationIdResolver, tokenResolver, texasClient);
    }

    @Test
    void shouldUseEntraIdOnBehalfOfForAzureSessions() {
        when(clientRegistrationIdResolver.getClientRegistrationId()).thenReturn(Mono.just(ResourceServerType.AZURE_AD));
        when(texasClient.exchange(TexasClient.ENTRA_ID, "api://dev-gcp.dolly.testnav-app/.default", "session-token"))
                .thenReturn(Mono.just(new AccessToken("obo")));

        assertThat(tokenExchange.exchange(serverProperties, exchange).block().getTokenValue()).isEqualTo("obo");
    }

    @Test
    void shouldUseTokenXForIdportenSessions() {
        when(clientRegistrationIdResolver.getClientRegistrationId()).thenReturn(Mono.just(ResourceServerType.TOKEN_X));
        when(texasClient.exchange(TexasClient.TOKENX, "dev-gcp:dolly:testnav-app", "session-token"))
                .thenReturn(Mono.just(new AccessToken("tokenx")));

        assertThat(tokenExchange.exchange(serverProperties, exchange).block().getTokenValue()).isEqualTo("tokenx");
    }
}
