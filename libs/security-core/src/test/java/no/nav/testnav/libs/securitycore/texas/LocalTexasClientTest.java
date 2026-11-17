package no.nav.testnav.libs.securitycore.texas;

import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalTexasClientTest {

    private final Queue<String> requests = new ConcurrentLinkedQueue<>();

    private DisposableServer server;
    private LocalTexasClient texasClient;

    @BeforeEach
    void setUp() {
        server = HttpServer.create()
                .port(0)
                .handle((request, response) -> request.receive().aggregate().asString()
                        .defaultIfEmpty("")
                        .flatMap(body -> {
                            requests.add(request.uri() + " " + body);
                            return response.header("Content-Type", "application/json")
                                    .sendString(Mono.just("{\"access_token\":\"token\"}"))
                                    .then();
                        }))
                .bindNow();
        var baseUrl = "http://localhost:" + server.port();
        var credential = mock(AzureClientCredential.class);
        when(credential.getTokenEndpoint()).thenReturn(baseUrl + "/entraid/oauth2/token");
        when(credential.getClientId()).thenReturn("local-client");
        when(credential.getClientSecret()).thenReturn("dummy");
        texasClient = new LocalTexasClient(baseUrl + "/api/v1/token", baseUrl + "/api/v1/token/exchange", null, credential, baseUrl, baseUrl, null);
    }

    @AfterEach
    void tearDown() {
        server.disposeNow();
    }

    @Test
    void shouldUseLocalauthForEntraIdClientCredentials() {
        texasClient.token(TexasClient.ENTRA_ID, "api://dev-gcp.dolly.app/.default").block();

        assertThat(requests.poll())
                .startsWith("/entraid/oauth2/token ")
                .contains("grant_type=client_credentials", "client_id=local-client");
    }

    @Test
    void shouldUseLocalauthForEntraIdOnBehalfOf() {
        texasClient.exchange(TexasClient.ENTRA_ID, "api://dev-gcp.dolly.app/.default", "user-token").block();

        assertThat(requests.poll())
                .startsWith("/entraid/oauth2/token ")
                .contains("grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer", "assertion=user-token");
    }

    @Test
    void shouldUseTexasProxyForTokenX() {
        texasClient.exchange(TexasClient.TOKENX, "dev-gcp:dolly:app", "user-token").block();

        assertThat(requests.poll()).startsWith("/api/v1/token/exchange ").contains("\"identity_provider\":\"tokenx\"");
    }
}
