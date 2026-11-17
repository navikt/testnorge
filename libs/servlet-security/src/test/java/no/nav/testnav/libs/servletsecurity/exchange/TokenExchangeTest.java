package no.nav.testnav.libs.servletsecurity.exchange;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenExchangeTest {

    private static final String AZURE_SCOPE = "api://dev-gcp.dolly.testnav-app/.default";
    private static final String TOKENX_TARGET = "dev-gcp:dolly:testnav-app";

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType = mock(GetAuthenticatedResourceServerType.class);
    private final GetAuthenticatedToken getAuthenticatedToken = mock(GetAuthenticatedToken.class);
    private final TexasClient texasClient = mock(TexasClient.class);
    private final ServerProperties serverProperties = new ServerProperties();

    private TokenExchange tokenExchange;

    @BeforeEach
    void setUp() {
        serverProperties.setName("testnav-app");
        serverProperties.setNamespace("dolly");
        serverProperties.setCluster("dev-gcp");
        serverProperties.setUrl("http://testnav-app.local");
        tokenExchange = new TokenExchange(getAuthenticatedResourceServerType, getAuthenticatedToken, texasClient);
    }

    @Test
    void shouldExchangeTokenForEachUser() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(ResourceServerType.AZURE_AD);
        when(getAuthenticatedToken.call()).thenReturn(userToken("user-a"), userToken("user-b"));
        when(texasClient.exchange(TexasClient.ENTRA_ID, AZURE_SCOPE, "user-a")).thenReturn(Mono.just(new AccessToken("obo-a")));
        when(texasClient.exchange(TexasClient.ENTRA_ID, AZURE_SCOPE, "user-b")).thenReturn(Mono.just(new AccessToken("obo-b")));

        assertThat(tokenExchange.exchange(serverProperties).block().getTokenValue()).isEqualTo("obo-a");
        assertThat(tokenExchange.exchange(serverProperties).block().getTokenValue()).isEqualTo("obo-b");
    }

    @Test
    void shouldUseClientCredentialsForApplications() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(ResourceServerType.AZURE_AD);
        when(getAuthenticatedToken.call()).thenReturn(Token.builder().accessTokenValue("app").clientCredentials(true).build());
        when(texasClient.token(TexasClient.ENTRA_ID, AZURE_SCOPE)).thenReturn(Mono.just(new AccessToken("cc")));

        assertThat(tokenExchange.exchange(serverProperties).block().getTokenValue()).isEqualTo("cc");
    }

    @Test
    void shouldUseTokenXForTokenXUsers() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(ResourceServerType.TOKEN_X);
        when(getAuthenticatedToken.call()).thenReturn(userToken("idporten-user"));
        when(texasClient.exchange(TexasClient.TOKENX, TOKENX_TARGET, "idporten-user")).thenReturn(Mono.just(new AccessToken("tokenx")));

        assertThat(tokenExchange.exchange(serverProperties).block().getTokenValue()).isEqualTo("tokenx");
    }

    private static Token userToken(String value) {
        return Token.builder().accessTokenValue(value).clientCredentials(false).build();
    }
}
