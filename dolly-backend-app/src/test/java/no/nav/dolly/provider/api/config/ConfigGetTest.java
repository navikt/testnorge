package no.nav.dolly.provider.api.config;

import no.nav.dolly.domain.resultset.RsDollyProps;
import no.nav.dolly.provider.RestTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;

@DisplayName("GET /api/v1/config")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class})
class ConfigGetTest extends RestTestBase {

    private static final String ENDPOINT_BASE_URI = "/api/v1/config";

    @Test
    @DisplayName("Returnerer urler til andre tjenester")
    void shouldGetUrls() {
        RsDollyProps resp = sendRequest()
                .to(GET, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.OK, RsDollyProps.class);

        assertNotNull(resp);
        assertTrue(resp.getTpsfUrl().contains("tps"));
        assertTrue(resp.getSigrunStubUrl().contains("sigrunstub"));
        assertTrue(resp.getKrrStubUrl().contains("krrstub"));
        assertTrue(resp.getKodeverkUrl().contains("kodeverk"));
        assertTrue(resp.getArenaForvalterUrl().contains("arenaforvalter"));
    }
}
