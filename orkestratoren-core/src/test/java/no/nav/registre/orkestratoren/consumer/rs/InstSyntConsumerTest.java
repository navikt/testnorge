package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class InstSyntConsumerTest {

    @Autowired
    private InstSyntConsumer instSyntConsumer;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private SyntetiserInstRequest syntetiserInstRequest;
    private List<String> identer;
    private List<String> oppholdIder;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        oppholdIder = new ArrayList<>(Arrays.asList("123", "456"));
        syntetiserInstRequest = new SyntetiserInstRequest(AVSPILLERGRUPPE_ID, MILJOE, identer.size());
    }

    @Test
    public void shouldStartSyntetisering() {
        stubInstConsumerStartSyntetisering();

        ResponseEntity response = (ResponseEntity) instSyntConsumer.startSyntetisering(syntetiserInstRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void shouldDeleteIdenterFromInst() {
        stubInstConsumerSlettIdenter();

        SletteInstitusjonsoppholdResponse response = instSyntConsumer.slettIdenterFraInst(identer);

        assertThat(response.getIdenterMedOppholdIdSomBleSlettet().keySet(), IsIterableContainingInAnyOrder.containsInAnyOrder(identer.get(0), identer.get(1)));
        assertThat(response.getIdenterMedOppholdIdSomBleSlettet().get(identer.get(0)), IsIterableContainingInOrder.contains(oppholdIder.get(0)));
        assertThat(response.getIdenterMedOppholdIdSomBleSlettet().get(identer.get(1)), IsIterableContainingInOrder.contains(oppholdIder.get(1)));
    }

    private void stubInstConsumerStartSyntetisering() {
        stubFor(post(urlPathEqualTo("/inst/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                                + ",\"miljoe\":\"" + MILJOE + "\""
                                + ",\"antallNyeIdenter\":" + identer.size() + "}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubInstConsumerSlettIdenter() {
        stubFor(delete(urlEqualTo("/inst/api/v1/ident/batch?identer=" + identer.get(0) + "," + identer.get(1)))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n"
                                + "    \"identerMedOppholdIdSomIkkeKunneSlettes\": {},\n"
                                + "    \"identerMedOppholdIdSomBleSlettet\": {\n"
                                + "        \"" + identer.get(0) + "\": [\n"
                                + "            \"" + oppholdIder.get(0) + "\"\n"
                                + "        ],\n"
                                + "        \"" + identer.get(1) + "\": [\n"
                                + "            \"" + oppholdIder.get(1) + "\"\n"
                                + "        ]\n"
                                + "    }\n"
                                + "}")));
    }
}