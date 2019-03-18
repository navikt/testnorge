package no.nav.registre.tp.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private Long avspillgergruppeId = 1L;
    private String miljoe = "q2";
    private Integer antallPersoner = 3;
    private Integer minimumAlder = 13;

    private List<String> expectedFnrs = new ArrayList<String>() {{
        add("123");
        add("132");
        add("321");
    }};

    @Test
    public void getFnrs() {
        stubHodejegeren();
        List<String> fnrs = hodejegerenConsumer.getFnrs(new SyntetiseringsRequest(avspillgergruppeId, miljoe, antallPersoner));
        assertEquals(expectedFnrs.toString(), fnrs.toString());
    }

    private void stubHodejegeren() {
        stubFor(get("/hodejegeren/api/v1/levende-identer/" + avspillgergruppeId + "?miljoe=" + miljoe + "&antallPersoner=" + antallPersoner + "&minimumAlder=" + minimumAlder).willReturn(okJson(
                "[\"123\", \"132\", \"321\"]"
        )));
    }
}