package no.nav.testnav.libs.reactivesecurity.manager;

import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AudienceValidatorTest {

    private static final String CLIENT_ID = "abc-123-def-456";
    private static final String API_PREFIX_AUDIENCE = "api://" + CLIENT_ID;

    private NonBeanJwtDecoder.AudienceValidator validator;

    private Jwt buildJwtWithAudience(List<String> audience) {
        return new Jwt(
                "dummy-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256", "typ", "JWT"),
                Map.of(
                        "iss", "https://login.microsoftonline.com/test-tenant/v2.0",
                        "sub", "test-subject",
                        "aud", audience,
                        "exp", Instant.now().plusSeconds(3600),
                        "iat", Instant.now()
                )
        );
    }

    @BeforeEach
    void setUp() {
        var properties = new ResourceServerProperties() {
            @Override
            public ResourceServerType getType() {
                return ResourceServerType.AZURE_AD;
            }
        };
        properties.setAcceptedAudience(List.of(CLIENT_ID, "api:// " + CLIENT_ID));
        properties.setIssuerUri("https://login.microsoftonline.com/test-tenant/v2.0");

        var decoder = new NonBeanJwtDecoder(WebClient.create(), properties, null);
        validator = decoder.new AudienceValidator();
    }

    @Test
    void shouldAcceptTokenWithClientIdAudience() {
        var jwt = buildJwtWithAudience(List.of(CLIENT_ID));

        var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void shouldRejectTokenWithApiPrefixBecauseConfigHasSpaceTypo() {
        var jwt = buildJwtWithAudience(List.of(API_PREFIX_AUDIENCE));

        var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void shouldAcceptTokenWithMultipleAudiencesWhereOneMatches() {
        var jwt = buildJwtWithAudience(List.of("wrong-audience", CLIENT_ID));

        var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void shouldRejectTokenWithWrongAudience() {
        var jwt = buildJwtWithAudience(List.of("wrong-client-id"));

        var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().iterator().next().getErrorCode()).isEqualTo("invalid_token");
    }

    @Test
    void shouldRejectTokenWithEmptyAudience() {
        var jwt = buildJwtWithAudience(List.of());

        var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void shouldAcceptTokenWithSpacedApiPrefixMatchingConfig() {
        var jwt = buildJwtWithAudience(List.of("api:// " + CLIENT_ID));

        var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void shouldRejectTokenWithDifferentClientId() {
        var jwt = buildJwtWithAudience(List.of("api://different-client-id"));

        var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }
}
