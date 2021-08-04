package no.nav.registre.testnorge.synt.person.provider;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import no.nav.testnav.libs.dto.person.v1.AdresseDTO;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;
import no.nav.registre.testnorge.synt.person.consumer.dto.SyntPersonDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class SyntPersonControllerIntegraiontestTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setup() throws Exception {
        JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
        Mockito.when(authentication.getCredentials())
                .thenReturn(Jwt
                        .withTokenValue("dummy_token")
                        .expiresAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(1).toInstant(ZoneOffset.UTC))
                        .header("dummy", "dummy")
                        .build()
                );

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/oauth2/v2.0/token")
                .withResponseBody(new AccessToken("dummy_token"))
                .stubPost();
    }

    @Test
    void should_create_synt_person() throws Exception {
        var syntPerson = SyntPersonDTO
                .builder()
                .fornavn("Hans")
                .slektsnavn("Hansen")
                .postnummer("0001")
                .adressenavn("Testveien")
                .kommunenummer("1000")
                .build();

        String ident = "01010112002";
        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/identifikator")
                .withResponseBody(Collections.singletonList(ident))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/generate/tps/(.*)")
                .withQueryParam("numToGenerate", "1")
                .withResponseBody(Collections.singletonList(syntPerson))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/personer")
                .stubPost();

        mvc.perform(post("/api/v1/synt-person")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        PersonDTO request = PersonDTO
                .builder()
                .fornavn(syntPerson.getFornavn())
                .etternavn(syntPerson.getSlektsnavn())
                .adresse(AdresseDTO
                        .builder()
                        .postnummer(syntPerson.getPostnummer())
                        .kommunenummer(syntPerson.getKommunenummer())
                        .gatenavn(syntPerson.getAdressenavn())
                        .build()
                )
                .ident(ident)
                .build();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/personer")
                .withRequestBody(request)
                .verifyPost();

    }

    @AfterEach
    void cleanup() {
        reset();
    }

}