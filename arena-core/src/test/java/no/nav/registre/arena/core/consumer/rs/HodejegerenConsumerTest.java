package no.nav.registre.arena.core.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.tomakehurst.wiremock.http.Fault;
import no.nav.registre.arena.core.config.AppConfig;
import no.nav.registre.arena.core.provider.rs.requests.ArenaSaveInHodejegerenRequest;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.domain.NyBruker;
import static no.nav.registre.arena.core.testutils.ResourceUtils.getResourceFileContent;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8082)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {HodejegerenConsumer.class, AppConfig.class})
@EnableAutoConfiguration
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private int MINIMUM_ALDER = 16;
    private long gruppeId = 10L;
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";

    @Test
    public void shouldFindLevendeIdenterOverAlder() {
        stubHodejegerenConsumer();

        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(gruppeId);

        assertThat(levendeIdenter, hasItem(fnr1));
        assertThat(levendeIdenter, hasItem(fnr2));
    }

    @Test
    public void shouldReturnEmptyOnEmptyResponse() {
        stubHodejegerenConsumerWithEmptyBody();

        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(gruppeId);

        assertThat(levendeIdenter, is(Collections.EMPTY_LIST));
    }

    @Test
    public void saveHistoryTest() {
        stubHodejegerenConsumerHistorikk();

        NyBruker bruker1 = new NyBruker(
                fnr1, "q2", "IKVAL", null,
                true, null, null);
        NyBruker bruker2 = new NyBruker(
                fnr2, "q2", "IKVAL", null,
                true, null, null);

        List<IdentMedData> identerMedData = new ArrayList<>();
        identerMedData.add(new IdentMedData(bruker1.getPersonident(), Collections.singletonList(bruker1)));
        identerMedData.add(new IdentMedData(bruker2.getPersonident(), Collections.singletonList(bruker2)));

        ArenaSaveInHodejegerenRequest request = new ArenaSaveInHodejegerenRequest("arena-forvalteren", identerMedData);
        List<String> lagredeIdenter = hodejegerenConsumer.saveHistory(request);

        assertThat(lagredeIdenter.contains(fnr1), is(true));
        assertThat(lagredeIdenter.contains(fnr2), is(true));
        assertThat(lagredeIdenter.size(), is(2));
        assertThat(lagredeIdenter.contains("30303030303"), is(false));
    }

    private void stubHodejegerenConsumerHistorikk() {
        stubFor(post(urlEqualTo("/hodejegeren/api/v1/historikk/"))
                .withRequestBody(equalToJson(
                        "{" +
                                "  \"kilde\": \"arena-forvalteren\"," +
                                "  \"identMedData\": [" +
                                "    {" +
                                "      \"id\": \"10101010101\"," +
                                "      \"data\": [" +
                                "        {" +
                                "          \"personident\": \"10101010101\"," +
                                "          \"miljoe\": \"q2\"," +
                                "          \"kvalifiseringsgruppe\": \"IKVAL\"," +
                                "          \"utenServicebehov\": null," +
                                "          \"automatiskInnsendingAvMeldekort\": true," +
                                "          \"aap115\": null," +
                                "          \"aap\": null" +
                                "        }" +
                                "      ]" +
                                "    }," +
                                "    {" +
                                "      \"id\": \"20202020202\"," +
                                "      \"data\": [" +
                                "        {" +
                                "          \"personident\": \"20202020202\"," +
                                "          \"miljoe\": \"q2\"," +
                                "          \"kvalifiseringsgruppe\": \"IKVAL\"," +
                                "          \"utenServicebehov\": null," +
                                "          \"automatiskInnsendingAvMeldekort\": true," +
                                "          \"aap115\": null," +
                                "          \"aap\": null" +
                                "        }" +
                                "      ]" +
                                "    }" +
                                "  ]" +
                                "}"
                ))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubHodejegerenConsumer() {
        stubFor(get(urlEqualTo("/hodejegeren/api/v1/levende-identer-over-alder/" + gruppeId + "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }

    private void stubHodejegerenConsumerWithEmptyBody() {
        stubFor(get(urlEqualTo("/hodejegeren/api/v1/levende-identer-over-alder/" + gruppeId + "?minimumAlder=" + MINIMUM_ALDER))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}

