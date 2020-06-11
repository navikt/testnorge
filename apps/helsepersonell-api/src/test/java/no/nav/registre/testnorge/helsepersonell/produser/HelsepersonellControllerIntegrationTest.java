package no.nav.registre.testnorge.helsepersonell.produser;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeDTO;
import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeListeDTO;
import no.nav.registre.testnorge.dto.hodejegeren.v1.PersondataDTO;
import no.nav.registre.testnorge.dto.samhandlerregisteret.v1.IdentDTO;
import no.nav.registre.testnorge.dto.samhandlerregisteret.v1.SamhandlerDTO;
import no.nav.registre.testnorge.test.JsonTestHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class HelsepersonellControllerIntegrationTest {


    @Value("${avspillingsgruppe.leger.id}")
    private Integer legerAvspillergruppeId;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void should_get_leger_with_hpr_id() throws Exception {

        String firstPersonIdent = "12125678903";
        String secondPersonIdent = "09126543211";
        List<String> leger = Arrays.asList(firstPersonIdent, secondPersonIdent);
        JsonTestHelper.stubGet(
                urlPathMatching("(.*)/v1/alle-identer/" + legerAvspillergruppeId),
                leger,
                objectMapper
        );

        JsonTestHelper.stubGet(
                WireMock.get(urlPathMatching("(.*)/v1/persondata"))
                        .withQueryParam("ident", WireMock.equalTo(firstPersonIdent)),
                PersondataDTO.builder().fnr(firstPersonIdent).fornavn("Hans").etternavn("Hansen").build(),
                objectMapper
        );

        JsonTestHelper.stubGet(
                WireMock.get(urlPathMatching("(.*)/v1/persondata"))
                        .withQueryParam("ident", WireMock.equalTo(secondPersonIdent)),
                PersondataDTO.builder().fnr(secondPersonIdent).fornavn("Berg").mellomnavn("Skog").etternavn("Fjell").build(),
                objectMapper
        );

        JsonTestHelper.stubGet(
                WireMock.get(urlPathMatching("(.*)/rest/sar/samh"))
                        .withQueryParam("ident", WireMock.equalTo(firstPersonIdent)),
                Collections.singletonList(SamhandlerDTO
                        .builder()
                        .identer(Collections.singletonList(IdentDTO.builder().identTypeKode("HPR").ident("54321").build()))
                        .build()),
                objectMapper
        );

        JsonTestHelper.stubGet(
                WireMock.get(urlPathMatching("(.*)/rest/sar/samh"))
                        .withQueryParam("ident", WireMock.equalTo(secondPersonIdent)),
                Collections.singletonList(SamhandlerDTO
                        .builder()
                        .identer(Collections.singletonList(IdentDTO.builder().identTypeKode("HPR").ident("12345").build()))
                        .build()),
                objectMapper
        );


        String json = mvc.perform(get("/api/v1/helepersonell/leger")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        LegeListeDTO acual = objectMapper.readValue(json, LegeListeDTO.class);

        assertThat(acual.getLeger()).containsOnly(
                LegeDTO.builder().fornavn("Hans").etternavn("Hansen").fnr(firstPersonIdent).hprId("54321").build(),
                LegeDTO.builder().fornavn("Berg").mellomnavn("Skog").etternavn("Fjell").fnr(secondPersonIdent).hprId("12345").build()
        );
    }


    @AfterEach
    public void cleanUp() {
        WireMock.reset();
    }


}