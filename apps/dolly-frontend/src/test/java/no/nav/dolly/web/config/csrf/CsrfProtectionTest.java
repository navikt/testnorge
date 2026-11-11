package no.nav.dolly.web.config.csrf;

import no.nav.dolly.web.consumers.Altinn3PersonOrganisasjonTilgangConsumer;
import no.nav.dolly.web.provider.web.SessionController;
import no.nav.dolly.web.service.BrukerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CsrfProtectionTest {

    private static final String SESSION_COOKIE = "SESSION";

    @Test
    void shouldRejectPostWithoutToken() {
        client().post().uri("/api/echo")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().valueEquals(CsrfProtection.CSRF_HEADER, CsrfProtection.CSRF_INVALID);
    }

    @Test
    void shouldAnswerBadRequestForClientLogWithoutToken() {
        client().post().uri("/api/dolly-logg")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().valueEquals(CsrfProtection.CSRF_HEADER, CsrfProtection.CSRF_INVALID);
    }

    @Test
    void shouldAcceptPostWithTokenFromSessionEndpoint() {
        var client = client();
        var csrf = fetchToken(client);

        client.post().uri("/api/echo")
                .cookie(SESSION_COOKIE, csrf.session())
                .header(SessionCsrfTokenRepository.HEADER_NAME, csrf.token())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRejectTokenFromAnotherSession() {
        var client = client();
        var victim = fetchToken(client);
        var attacker = fetchToken(client);

        client.post().uri("/api/echo")
                .cookie(SESSION_COOKIE, victim.session())
                .header(SessionCsrfTokenRepository.HEADER_NAME, attacker.token())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldIgnoreFormParameterOutsideLogout() {
        var client = client();
        var csrf = fetchToken(client);

        client.post().uri("/api/echo")
                .cookie(SESSION_COOKIE, csrf.session())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(SessionCsrfTokenRepository.PARAMETER_NAME, csrf.token()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldAcceptFormParameterForLogout() {
        var client = client();
        var csrf = fetchToken(client);

        client.post().uri("/logout")
                .cookie(SESSION_COOKIE, csrf.session())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(SessionCsrfTokenRepository.PARAMETER_NAME, csrf.token()))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/login?logout");
    }

    @Test
    void shouldRedirectToAppWhenLogoutTokenIsInvalid() {
        var client = client();
        var csrf = fetchToken(client);

        client.post().uri("/logout")
                .cookie(SESSION_COOKIE, csrf.session())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(SessionCsrfTokenRepository.PARAMETER_NAME, "ugyldig"))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/");
    }

    @Test
    void shouldNotLogOutOnGet() {
        client().get().uri("/logout")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldAllowGetWithoutToken() {
        client().get().uri("/session/ping")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldNotCacheTokenResponse() {
        client().get().uri("/session/csrf")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().cacheControl(CacheControl.noStore());
    }

    private static CsrfSession fetchToken(WebTestClient client) {
        var result = client.get().uri("/session/csrf")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult();
        var session = result.getResponseCookies().getFirst(SESSION_COOKIE);
        assertThat(session).isNotNull();
        var body = result.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body.get("headerName")).isEqualTo(SessionCsrfTokenRepository.HEADER_NAME);
        assertThat(body.get("parameterName")).isEqualTo(SessionCsrfTokenRepository.PARAMETER_NAME);
        return new CsrfSession(session.getValue(), (String) body.get("token"));
    }

    private static WebTestClient client() {
        var http = CsrfProtection.apply(ServerHttpSecurity.http())
                .authorizeExchange(spec -> spec.anyExchange().permitAll())
                .logout(spec -> spec.logoutUrl("/logout"));
        var sessionController = new SessionController(mock(BrukerService.class), mock(Altinn3PersonOrganisasjonTilgangConsumer.class));
        return WebTestClient.bindToController(sessionController, new EchoController())
                .webFilter(new WebFilterChainProxy(http.build()))
                .build();
    }

    private record CsrfSession(String session, String token) {
    }

    @RestController
    static class EchoController {

        @PostMapping("/api/echo")
        Mono<ResponseEntity<Void>> echo() {
            return Mono.just(ResponseEntity.ok().build());
        }
    }
}
