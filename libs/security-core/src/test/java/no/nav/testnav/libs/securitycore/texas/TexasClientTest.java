package no.nav.testnav.libs.securitycore.texas;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TexasClientTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final Queue<RecordedRequest> requests = new ConcurrentLinkedQueue<>();
    private final Queue<StubResponse> responses = new ConcurrentLinkedQueue<>();

    private DisposableServer server;
    private TexasClient texasClient;

    @BeforeEach
    void setUp() {
        server = HttpServer.create()
                .port(0)
                .handle((request, response) -> request.receive().aggregate().asString()
                        .defaultIfEmpty("")
                        .flatMap(body -> {
                            requests.add(new RecordedRequest(request.uri(), request.requestHeaders().get("Authorization"), body));
                            var stub = responses.poll();
                            return response.status(stub.status())
                                    .header("Content-Type", "application/json")
                                    .sendString(Mono.just(stub.body()))
                                    .then();
                        }))
                .bindNow();
        texasClient = texasClient(null);
    }

    private TexasClient texasClient(String localSecret) {
        var baseUrl = "http://localhost:" + server.port();
        return new TexasClient(baseUrl + "/api/v1/token", baseUrl + "/api/v1/token/exchange", baseUrl + "/api/v1/introspect", localSecret);
    }

    @AfterEach
    void tearDown() {
        server.disposeNow();
    }

    @Test
    void shouldRequestTokenFromTokenEndpoint() {
        responses.add(ok("client-token"));

        var token = texasClient.token(TexasClient.ENTRA_ID, "api://dev-gcp.dolly.app/.default").block();

        assertThat(token.getTokenValue()).isEqualTo("client-token");
        var request = requests.poll();
        assertThat(request.uri()).isEqualTo("/api/v1/token");
        assertThat(request.json()).isEqualTo(JSON.readTree("""
                {"identity_provider":"entra_id","target":"api://dev-gcp.dolly.app/.default"}
                """));
        assertThat(request.authorization()).isNull();
    }

    @Test
    void shouldSendSharedSecretToLocalTexasProxy() {
        responses.add(ok("client-token"));

        texasClient("LOCAL").token(TexasClient.ENTRA_ID, "api://dev-gcp.dolly.app/.default").block();

        assertThat(requests.poll().authorization()).isEqualTo("Dolly LOCAL");
    }

    @Test
    void shouldExchangeUserTokenAtExchangeEndpoint() {
        responses.add(ok("exchanged-token"));

        var token = texasClient.exchange(TexasClient.TOKENX, "dev-gcp:dolly:app", "user-token").block();

        assertThat(token.getTokenValue()).isEqualTo("exchanged-token");
        var request = requests.poll();
        assertThat(request.uri()).isEqualTo("/api/v1/token/exchange");
        assertThat(request.json()).isEqualTo(JSON.readTree("""
                {"identity_provider":"tokenx","target":"dev-gcp:dolly:app","user_token":"user-token"}
                """));
    }

    @Test
    void shouldNotRetryClientErrors() {
        responses.add(new StubResponse(HttpResponseStatus.BAD_REQUEST, """
                {"error":"invalid_request","error_description":"ugyldig target"}
                """));

        var token = texasClient.token(TexasClient.ENTRA_ID, "api://dev-gcp.dolly.app/.default");

        assertThatThrownBy(token::block).isInstanceOf(WebClientResponseException.BadRequest.class);
        assertThat(requests).hasSize(1);
    }

    @Test
    void shouldFailWithOriginalErrorWhenRetriesAreExhausted() {
        responses.addAll(List.of(
                new StubResponse(HttpResponseStatus.SERVICE_UNAVAILABLE, "{}"),
                new StubResponse(HttpResponseStatus.SERVICE_UNAVAILABLE, "{}"),
                new StubResponse(HttpResponseStatus.SERVICE_UNAVAILABLE, "{}")));

        var token = texasClient.token(TexasClient.ENTRA_ID, "api://dev-gcp.dolly.app/.default");

        assertThatThrownBy(token::block).isInstanceOf(WebClientResponseException.ServiceUnavailable.class);
        assertThat(requests).hasSize(3);
    }

    @Test
    void shouldReturnClaimsWhenTexasConsidersTokenActive() {
        responses.add(new StubResponse(HttpResponseStatus.OK, """
                {"active":true,"sub":"subject","aud":"app"}
                """));

        var claims = texasClient.introspect(TexasClient.ENTRA_ID, "user-token").block();

        assertThat(claims).containsEntry("sub", "subject").containsEntry("aud", "app");
        var request = requests.poll();
        assertThat(request.uri()).isEqualTo("/api/v1/introspect");
        assertThat(request.json()).isEqualTo(JSON.readTree("""
                {"identity_provider":"entra_id","token":"user-token"}
                """));
    }

    @Test
    void shouldRejectTokenThatTexasConsidersInactive() {
        responses.add(new StubResponse(HttpResponseStatus.OK, """
                {"active":false,"error":"token is expired"}
                """));

        var claims = texasClient.introspect(TexasClient.TOKENX, "user-token");

        assertThatThrownBy(claims::block)
                .isInstanceOf(BadJwtException.class)
                .hasMessage("token is expired");
    }

    private static StubResponse ok(String accessToken) {
        return new StubResponse(HttpResponseStatus.OK, """
                {"access_token":"%s","expires_in":3599,"token_type":"Bearer"}
                """.formatted(accessToken));
    }

    private record StubResponse(HttpResponseStatus status, String body) {
    }

    private record RecordedRequest(String uri, String authorization, String body) {

        JsonNode json() {
            return JSON.readTree(body);
        }
    }
}
