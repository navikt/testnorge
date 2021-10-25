package no.nav.registre.testnorge.helsepersonellservice.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellDTO;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.testnav.libs.dto.hodejegeren.v1.PersondataDTO;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.IdentDTO;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;
import no.nav.testnav.libs.testing.JsonWiremockHelper;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
@Disabled
public class HelsepersonellControllerIntegrationTest {


    @MockBean
    public JwtDecoder jwtDecoder;

    @Value("${avspillingsgruppe.helsepersonell.id}")
    private Integer helsepersonellAvspillergruppeId;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_get_helsepersonell_with_hpr_id() throws Exception {

        String firstPersonIdent = "12125678903";
        String secondPersonIdent = "09126543211";
        List<String> helsepersonell = Arrays.asList(firstPersonIdent, secondPersonIdent);


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/alle-identer/" + helsepersonellAvspillergruppeId)
                .withResponseBody(helsepersonell)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/persondata")
                .withQueryParam("ident", firstPersonIdent)
                .withResponseBody(PersondataDTO
                        .builder()
                        .fnr(firstPersonIdent)
                        .fornavn("Hans")
                        .etternavn("Hansen")
                        .build())
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/persondata")
                .withQueryParam("ident", secondPersonIdent)
                .withResponseBody(PersondataDTO
                        .builder()
                        .fnr(secondPersonIdent)
                        .fornavn("Berg")
                        .mellomnavn("Skog")
                        .etternavn("Fjell")
                        .build())
                .stubGet();


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/rest/sar/samh")
                .withQueryParam("ident", firstPersonIdent)
                .withResponseBody(
                        Collections.singletonList(SamhandlerDTO
                                .builder()
                                .kode("LE")
                                .identer(Arrays.asList(
                                        IdentDTO
                                                .builder()
                                                .identTypeKode("HPR")
                                                .ident("54321")
                                                .build(),
                                        IdentDTO
                                                .builder()
                                                .identTypeKode("FNR")
                                                .ident(firstPersonIdent)
                                                .build()
                                ))
                                .build()
                        ))
                .stubGet();


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/rest/sar/samh")
                .withQueryParam("ident", secondPersonIdent)
                .withResponseBody(
                        Collections.singletonList(SamhandlerDTO
                                .builder()
                                .kode("LE")
                                .identer(Arrays.asList(
                                        IdentDTO
                                                .builder()
                                                .identTypeKode("HPR")
                                                .ident("12345")
                                                .build(),
                                        IdentDTO
                                                .builder()
                                                .identTypeKode("FNR")
                                                .ident(secondPersonIdent)
                                                .build()
                                ))
                                .build()
                        ))
                .stubGet();


        String json = mvc.perform(get("/api/v1/helsepersonell")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        HelsepersonellListeDTO actual = objectMapper.readValue(json, HelsepersonellListeDTO.class);

        assertThat(actual.getHelsepersonell()).containsOnly(
                HelsepersonellDTO.builder().fornavn("Hans").etternavn("Hansen").fnr(firstPersonIdent).hprId("54321").samhandlerType("LE").build(),
                HelsepersonellDTO.builder().fornavn("Berg").mellomnavn("Skog").etternavn("Fjell").fnr(secondPersonIdent).hprId("12345").samhandlerType("LE").build()
        );
    }


    @AfterEach
    public void cleanUp() {
        WireMock.reset();
    }


}