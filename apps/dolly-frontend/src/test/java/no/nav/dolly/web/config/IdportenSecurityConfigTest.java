package no.nav.dolly.web.config;

import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

class IdportenSecurityConfigTest {

    private static final String ISSUER = "https://test.idporten.no";
    private static final String CLIENT_ID = "dolly-idporten";

    private final ClientRegistration registration = ClientRegistration
            .withRegistrationId("idporten")
            .clientId(CLIENT_ID)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid")
            .authorizationUri(ISSUER + "/authorize")
            .tokenUri(ISSUER + "/token")
            .jwkSetUri(ISSUER + "/jwks.json")
            .issuerUri(ISSUER)
            .build();

    @Test
    void shouldRequestLoaHighWithPkce() {
        var config = new IdportenSecurityConfig("well-known", "post-logout", "jwk");
        var resolver = config.pkceResolver(new InMemoryReactiveClientRegistrationRepository(registration));
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/oauth2/authorization/idporten"));

        var request = resolver.resolve(exchange).block();

        assertThat(request).isNotNull();
        assertThat(request.getAdditionalParameters())
                .containsEntry("acr_values", "idporten-loa-high")
                .containsKey(PkceParameterNames.CODE_CHALLENGE);
    }

    @Test
    void shouldAcceptIdTokenWithLoaHigh() {
        assertThat(validate("idporten-loa-high").hasErrors()).isFalse();
    }

    @Test
    void shouldRejectIdTokenWithLoaSubstantial() {
        assertThat(validate("idporten-loa-substantial").hasErrors()).isTrue();
    }

    @Test
    void shouldRejectIdTokenWithoutAcr() {
        assertThat(validate(null).hasErrors()).isTrue();
    }

    @Test
    void shouldStillRunStandardOidcValidation() {
        var now = Instant.now();
        var tokenForOtherClient = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer(ISSUER)
                .subject("pseudonym")
                .audience(List.of("annen-klient"))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(120))
                .claim("acr", "idporten-loa-high")
                .build();

        assertThat(IdportenSecurityConfig.idTokenValidator(registration).validate(tokenForOtherClient).hasErrors())
                .isTrue();
    }

    private OAuth2TokenValidatorResult validate(String acr) {
        var now = Instant.now();
        var builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer(ISSUER)
                .subject("pseudonym")
                .audience(List.of(CLIENT_ID))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(120));
        if (nonNull(acr)) {
            builder.claim("acr", acr);
        }
        return IdportenSecurityConfig.idTokenValidator(registration).validate(builder.build());
    }

    @Test
    @SneakyThrows
    void shouldBuildSecurityChainAndRedirectUnauthenticatedNavigationToLoginPage() {
        var repository = new InMemoryReactiveClientRegistrationRepository(registration);
        var config = new IdportenSecurityConfig("well-known", "post-logout", new RSAKeyGenerator(2048).generate().toJSONString());

        var chain = config.configure(WebClient.create(), securityWithClientRegistrations(repository), config.pkceResolver(repository));

        assertRedirectsToLoginPage(chain);
    }

    static ServerHttpSecurity securityWithClientRegistrations(ReactiveClientRegistrationRepository repository) {
        var context = new GenericApplicationContext();
        context.registerBean(ReactiveClientRegistrationRepository.class, () -> repository);
        context.refresh();
        return new ServerHttpSecurity() {
            {
                setApplicationContext(context);
            }
        };
    }

    static void assertRedirectsToLoginPage(SecurityWebFilterChain chain) {
        WebTestClient
                .bindToWebHandler(exchange -> Mono.empty())
                .webFilter(new WebFilterChainProxy(chain))
                .build()
                .get()
                .uri("/gruppe")
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/login");
    }
}
