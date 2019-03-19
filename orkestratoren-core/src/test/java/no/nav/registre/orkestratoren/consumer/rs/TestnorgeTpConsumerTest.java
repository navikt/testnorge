package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class TestnorgeTpConsumerTest {

    @Autowired
    private TestnorgeTpConsumer testnorgeTpConsumer;

    private Long gruppeId = 10L;
    private String miljoe = "t9";
    private Integer antallPersoner = 3;

    @Test
    public void startSyntetisering() {
        stubTp();
        ResponseEntity entity = testnorgeTpConsumer.startSyntetisering(new SyntetiserTpRequest(gruppeId, miljoe, antallPersoner));
        assertEquals(HttpStatus.OK, entity.getStatusCode());

    }

    private void stubTp() {
        stubFor(post(urlPathEqualTo("/tp/api/v1/syntetisering/generer/"))
                .withRequestBody(equalToJson("{\"avspillergruppeId\": " + gruppeId +
                        ",\"miljoe\":\"" + miljoe + "\"" +
                        ",\"antallPersoner\":" + antallPersoner + "}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json"))
        );
    }
}