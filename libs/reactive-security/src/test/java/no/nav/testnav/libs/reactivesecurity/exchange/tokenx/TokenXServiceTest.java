package no.nav.testnav.libs.reactivesecurity.exchange.tokenx;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenXServiceTest {

    private static final String TARGET = "dev-gcp:dolly:testnav-app";

    private final GetAuthenticatedToken getAuthenticatedToken = mock(GetAuthenticatedToken.class);
    private final TexasClient texasClient = mock(TexasClient.class);
    private final ServerProperties serverProperties = new ServerProperties();

    private TokenXService tokenXService;

    @BeforeEach
    void setUp() {
        serverProperties.setName("testnav-app");
        serverProperties.setUrl("http://testnav-app.local");
        tokenXService = new TokenXService(getAuthenticatedToken, texasClient);
    }

    @Test
    void shouldExchangeGivenTokenWithTexas() {
        when(texasClient.exchange(TexasClient.TOKENX, TARGET, "fake-token")).thenReturn(Mono.just(new AccessToken("tokenx-token")));

        var accessToken = tokenXService.exchange(serverProperties, "fake-token").block();

        assertThat(accessToken.getTokenValue()).isEqualTo("tokenx-token");
    }
}
