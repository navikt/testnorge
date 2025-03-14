package no.nav.dolly.web.config;

import com.nimbusds.jose.jwk.JWK;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.authentication.DollyAuthenticationSuccessHandler;
import no.nav.testnav.libs.reactivesessionsecurity.handler.LogoutSuccessHandler;
import no.nav.testnav.libs.reactivesessionsecurity.manager.AuthorizationCodeReactiveAuthenticationManger;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.logut.IdportenOcidLogoutUrlResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Configuration
@Profile("idporten")
@EnableWebFluxSecurity
class IdportenSecurityConfig {

    private static final String LOGOUT = "/logout";
    private static final String LOGIN = "/login";
    private final String jwk;
    private final String wellKnownUrl;
    private final String postLogoutRedirectUri;

    public IdportenSecurityConfig(
            @Value("${spring.security.oauth2.client.provider.idporten.issuer-uri}/.well-known/openid-configuration") String wellKnownUrl,
            @Value("${spring.security.oauth2.client.registration.idporten.post-logout-redirect-uri}") String postLogoutRedirectUri,
            @Value("${IDPORTEN_CLIENT_JWK}") String jwk
    ) {
        this.jwk = jwk;
        this.wellKnownUrl = wellKnownUrl;
        this.postLogoutRedirectUri = postLogoutRedirectUri;
    }

    @Bean
    ServerOAuth2AuthorizationRequestResolver pkceResolver(ReactiveClientRegistrationRepository repo) {
        var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(repo);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    @SneakyThrows
    @Bean
    SecurityWebFilterChain configure(
            WebClient webClient,
            ServerHttpSecurity http,
            ServerOAuth2AuthorizationRequestResolver requestResolver
    ) {
        var authenticationSuccessHandler = new DollyAuthenticationSuccessHandler();
        var authenticationManager = new AuthorizationCodeReactiveAuthenticationManger(webClient, JWK.parse(jwk));
        var logoutSuccessHandler = new LogoutSuccessHandler();
        logoutSuccessHandler.applyOn("idporten", new IdportenOcidLogoutUrlResolver(webClient, wellKnownUrl, postLogoutRedirectUri));

        return http
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers(
                                "/*.css",
                                "/*.js",
                                "/*.mjs",
                                "/*.png",
                                "/assets/*",
                                "/favicon.ico",
                                "/internal/**",
                                "/oauth2/callback",
                                "/oauth2/logout",
                                LOGIN,
                                LOGOUT
                        ).permitAll()
                        .anyExchange().authenticated())
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            log.error("Failed to authenticate user", exception);
                            return Mono.error(exception);
                        })
                        .authenticationManager(authenticationManager)
                        .authorizationRequestResolver(requestResolver)
                        .authenticationSuccessHandler(authenticationSuccessHandler))
                .formLogin(formLoginSpec -> formLoginSpec.loginPage(LOGIN).authenticationFailureHandler((webFilterExchange, exception) -> {
                    log.error("Failed to authenticate user", exception);
                    return Mono.error(exception);
                }))
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl(LOGOUT)
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, LOGOUT))
                        .logoutSuccessHandler(logoutSuccessHandler))
                .build();
    }
}
