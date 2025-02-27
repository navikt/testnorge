package no.nav.testnav.libs.standalone.servletsecurity.jwt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.standalone.servletsecurity.domain.ResourceServerType;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.AzureAdTokenService;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@AutoConfiguration
@Import({
        TokenExchange.class,
        AzureAdTokenService.class
})
class InsecureJwtServerToServerAutoConfiguration {

    @Bean
    @Profile("!test")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("no.nav.testnav.libs.servletsecurity.jwt.MultipleIssuersJwtDecoder")
        // TODO: These two identical implementations should join forces in servlet-core.
    JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.aad.issuer-uri:#{null}") String azureIssuerUri,
            @Value("${spring.security.oauth2.resourceserver.aad.accepted-audience:#{null}") List<String> azureAcceptedAudience,
            @Value("${spring.security.oauth2.resourceserver.tokenx.issuer-uri:#{null}") String tokenXIssuerUri,
            @Value("${spring.security.oauth2.resourceserver.tokenx.accepted-audience:#{null}") List<String> tokenXAcceptedAudience
    ) {
        var azure = Azure.ifNonNull(azureIssuerUri, azureAcceptedAudience);
        var tokenX = TokenX.ifNonNull(tokenXIssuerUri, tokenXAcceptedAudience);
        var list = Stream
                .of(azure, tokenX)
                .filter(Objects::nonNull)
                .toList();
        if (list.isEmpty()) {
            throw new IllegalStateException("Missing required aad/tokenx configuration under spring.security.oauth2.resourceserver");
        }
        return new MultipleIssuersJwtDecoder(list);
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("no.nav.testnav.libs.servletsecurity.jwt.NoopJwtDecoder")
        // TODO: These two identical implementations should join forces in servlet-core.
    JwtDecoder jwtDecoderForTesting() {
        return new NoopJwtDecoder();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class Azure implements ResourceServerProperties {

        private final String issuerUri;
        private final List<String> acceptedAudience;
        private final ResourceServerType type = ResourceServerType.AZURE_AD;

        private static Azure ifNonNull(@Nullable String issuerUri, @Nullable List<String> acceptedAudience) {
            return issuerUri == null || acceptedAudience == null ?
                    null :
                    new Azure(issuerUri, acceptedAudience);
        }

    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class TokenX implements ResourceServerProperties {

        private final String issuerUri;
        private final List<String> acceptedAudience;
        private final ResourceServerType type = ResourceServerType.TOKEN_X;

        private static TokenX ifNonNull(@Nullable String issuerUri, @Nullable List<String> acceptedAudience) {
            return issuerUri == null || acceptedAudience == null ?
                    null :
                    new TokenX(issuerUri, acceptedAudience);
        }

    }

    interface ResourceServerProperties {

        String getIssuerUri();

        List<String> getAcceptedAudience();

        ResourceServerType getType();

    }
}