package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AzureTokenServiceTest {

    private static final String SCOPE = "api://dev-gcp.dolly.testnav-app/.default";

    private final GetAuthenticatedToken getAuthenticatedToken = mock(GetAuthenticatedToken.class);
    private final TexasClient texasClient = mock(TexasClient.class);
    private final ServerProperties serverProperties = new ServerProperties();

    @BeforeEach
    void setUp() {
        serverProperties.setName("testnav-app");
        serverProperties.setUrl("http://testnav-app.local");
    }

    @Test
    void shouldUseTexasForClientCredentials() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(token(true, Instant.now().plusSeconds(600))));
        when(texasClient.token(TexasClient.ENTRA_ID, SCOPE)).thenReturn(Mono.just(new AccessToken("texas-token")));

        var accessToken = service(texasClient).exchange(serverProperties).block();

        assertThat(accessToken.getTokenValue()).isEqualTo("texas-token");
    }

    @Test
    void shouldUseTexasForOnBehalfOf() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(token(false, Instant.now().plusSeconds(600))));
        when(texasClient.exchange(TexasClient.ENTRA_ID, SCOPE, "user-token")).thenReturn(Mono.just(new AccessToken("obo-token")));

        var accessToken = service(texasClient).exchange(serverProperties).block();

        assertThat(accessToken.getTokenValue()).isEqualTo("obo-token");
    }

    @Test
    void shouldRejectTokensThatExpireSoonBeforeCallingTexas() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(token(false, Instant.now().plusSeconds(60))));

        var accessToken = service(texasClient).exchange(serverProperties);

        assertThatThrownBy(accessToken::block).isInstanceOf(AccessDeniedException.class);
        verifyNoInteractions(texasClient);
    }

    private AzureTokenService service(TexasClient texas) {
        return new AzureTokenService(getAuthenticatedToken, texas);
    }

    private static Token token(boolean clientCredentials, Instant expiresAt) {
        return Token.builder()
                .accessTokenValue("user-token")
                .clientCredentials(clientCredentials)
                .expiresAt(expiresAt)
                .build();
    }
}
