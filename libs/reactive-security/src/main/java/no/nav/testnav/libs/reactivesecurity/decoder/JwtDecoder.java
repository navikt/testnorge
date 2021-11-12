package no.nav.testnav.libs.reactivesecurity.decoder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;

@Slf4j
@AllArgsConstructor
public class JwtDecoder {

    private final ResourceServerProperties resourceServerProperties;
    private final String proxyHost;

    public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            var error = new OAuth2Error(
                    "invalid_token",
                    String.format("None of required audience values '%s' found in token", resourceServerProperties.getAcceptedAudience()),
                    null
            );
            return  jwt.getAudience().stream().anyMatch(resourceServerProperties.getAcceptedAudience()::contains)
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(error);
        }
    }

    private WebClient webClient() {
        var builder = WebClient.builder();
        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for jwt decoder.", proxyHost);
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

    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder jwtDecoder = switch (resourceServerProperties.getType()) {
            case TOKEN_X -> NimbusReactiveJwtDecoder.withJwkSetUri(resourceServerProperties.getJwkSetUri()).build();
            case AZURE_AD -> NimbusReactiveJwtDecoder.withJwkSetUri(resourceServerProperties.getJwkSetUri()).webClient(webClient()).build();
        };

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(resourceServerProperties.getIssuerUri());
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }
}
