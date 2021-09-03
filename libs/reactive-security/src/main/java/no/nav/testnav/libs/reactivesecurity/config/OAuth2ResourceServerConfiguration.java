package no.nav.testnav.libs.reactivesecurity.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class OAuth2ResourceServerConfiguration {

    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    private final List<String> acceptedAudience;
    private final String proxyHost;

    public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            var error = new OAuth2Error("invalid_token", String.format("None of required audience values '%s' found in token", acceptedAudience), null);
            return jwt.getAudience().stream().anyMatch(acceptedAudience::contains)
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(error);
        }
    }

    public WebClient webClient() {
        var builder = WebClient.builder();
        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Client Credentials", proxyHost);
            var uri = URI.create(proxyHost);

            HttpClient httpClient = HttpClient
                    .create()
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort())
                    ));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
        return builder.build();
    }


    protected ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(oAuth2ResourceServerProperties.getJwt().getJwkSetUri())
                .webClient(webClient())
                .build();


        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    public abstract SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http);
}
