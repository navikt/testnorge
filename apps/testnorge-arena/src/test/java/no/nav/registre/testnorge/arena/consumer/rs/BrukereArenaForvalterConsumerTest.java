package no.nav.registre.testnorge.arena.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import no.nav.registre.testnorge.arena.consumer.rs.util.ArbeidssoekerCacheUtil;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class BrukereArenaForvalterConsumerTest {

    private BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;

    private MockWebServer mockWebServer;

    private String miljoe = "q2";

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        Dispatcher dispatcher = getArenaForvalterDispatcher();
        mockWebServer.setDispatcher(dispatcher);
        this.brukereArenaForvalterConsumer = new BrukereArenaForvalterConsumer(
                new ArbeidssoekerCacheUtil(),
                mockWebServer.url("/api").toString());
    }

    @Test
    public void checkEmptyListOnBadSentTilArenaForvalterRequest() {
        NyeBrukereResponse response = brukereArenaForvalterConsumer.sendTilArenaForvalter(null);

        assertThat(response.getArbeidsoekerList(), is(Collections.EMPTY_LIST));
    }

    @Test
    public void hentBrukereTest() {
        List<Arbeidsoeker> response = brukereArenaForvalterConsumer.hentArbeidsoekere("", "", "");

        assertThat(response.get(2).getPersonident(), is("09038817873"));
        assertThat(response.size(), is(3));
    }

    @Test
    public void slettBrukereTest() {
        var test = brukereArenaForvalterConsumer.slettBruker("20202020202", miljoe);
        assertThat(test, is(true));
    }

    @Test
    public void slettBrukereBadReq() {
        var test = brukereArenaForvalterConsumer.slettBruker("10101010101", miljoe);
        assertThat(test, is(false));
    }

    @Test
    public void getBrukereFilterTest() {
        List<Arbeidsoeker> response = brukereArenaForvalterConsumer.hentArbeidsoekere("10101010101", "Dolly", miljoe);

        assertThat(response.get(0).getStatus(), is("OK"));
        assertThat(response.size(), is(2));
        assertThat(response.get(1).getAap(), is(true));
    }

    @Test
    public void getBrukereFilterPersonident() {
        List<Arbeidsoeker> response = brukereArenaForvalterConsumer.hentArbeidsoekere("10101010101", "", "");
        response.addAll(brukereArenaForvalterConsumer.hentArbeidsoekere("20202020202", "", ""));

        assertThat(response.size(), is(4));
        assertThat(response.get(2).getPersonident(), is("10101010101"));
        assertThat(response.get(2).getMiljoe(), is("q0"));
        assertThat(response.get(1).getPersonident(), is("10101010101"));
        assertThat(response.get(3).getPersonident(), is("20202020202"));
    }

    private Dispatcher getArenaForvalterDispatcher(){
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/api/v1/bruker?eier=ORKESTRATOREN":
                        return new MockResponse().setResponseCode(400);
                    case "/api/v1/bruker":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "  \"arbeidsokerList\": [" +
                                        "    {" +
                                        "      \"personident\": \"07098524627\"," +
                                        "      \"miljoe\": \"q2\"," +
                                        "      \"status\": \"OK\"," +
                                        "      \"eier\": \"Dolly\"," +
                                        "      \"servicebehov\": false," +
                                        "      \"automatiskInnsendingAvMeldekort\": false," +
                                        "      \"aap115\": false," +
                                        "      \"aap\": false" +
                                        "    }," +
                                        "    {" +
                                        "      \"personident\": \"13119316876\"," +
                                        "      \"miljoe\": \"t4\"," +
                                        "      \"status\": \"OK\"," +
                                        "      \"eier\": \"Dolly\"," +
                                        "      \"servicebehov\": true," +
                                        "      \"automatiskInnsendingAvMeldekort\": true," +
                                        "      \"aap115\": true," +
                                        "      \"aap\": false" +
                                        "    }" +
                                        "  ]," +
                                        "  \"antallSider\": 2" +
                                        "}");
                    case "/api/v1/bruker?page=0":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "  \"arbeidsokerList\": [" +
                                        "    {" +
                                        "      \"personident\": \"07098524627\"," +
                                        "      \"miljoe\": \"q2\"," +
                                        "      \"status\": \"OK\"," +
                                        "      \"eier\": \"Dolly\"," +
                                        "      \"servicebehov\": false," +
                                        "      \"automatiskInnsendingAvMeldekort\": false," +
                                        "      \"aap115\": false," +
                                        "      \"aap\": false" +
                                        "    }," +
                                        "    {" +
                                        "      \"personident\": \"13119316876\"," +
                                        "      \"miljoe\": \"t4\"," +
                                        "      \"status\": \"OK\"," +
                                        "      \"eier\": \"Dolly\"," +
                                        "      \"servicebehov\": true," +
                                        "      \"automatiskInnsendingAvMeldekort\": true," +
                                        "      \"aap115\": false," +
                                        "      \"aap\": true" +
                                        "    }" +
                                        "  ]," +
                                        "  \"antallSider\": 2" +
                                        "}");
                    case "/api/v1/bruker?page=1":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "  \"arbeidsokerList\": [" +
                                        "    {" +
                                        "      \"personident\": \"09038817873\"," +
                                        "      \"miljoe\": \"q1\"," +
                                        "      \"status\": \"OK\"," +
                                        "      \"eier\": \"Dolly\"," +
                                        "      \"servicebehov\": true," +
                                        "      \"automatiskInnsendingAvMeldekort\": true," +
                                        "      \"aap115\": true," +
                                        "      \"aap\": true" +
                                        "    }" +
                                        "  ]," +
                                        "  \"antallSider\": 2" +
                                        "}");
                    case "/api/v1/bruker?miljoe=q2&personident=20202020202":
                        return new MockResponse().setResponseCode(200);
                    case "/api/v1/bruker?miljoe=q2&personident=10101010101":
                        return new MockResponse().setResponseCode(400)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "\"timestamp\": \"2019-07-03T07:45:19.109+0000\"," +
                                        "\"status\": 400," +
                                        "\"error\": \"Bad Request\"," +
                                        "\"message\": \"Identen er ikke registrert i arena-forvalteren\"," +
                                        "\"path\": \"/api/v1/bruker\"" +
                                        "}");
                    case "/api/v1/bruker?filter-eier=Dolly&filter-miljoe=q2&filter-personident=10101010101":
                    case "/api/v1/bruker?filter-eier=Dolly&filter-miljoe=q2&filter-personident=10101010101&page=0":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "\"arbeidsokerList\": [{" +
                                        "\"personident\": \"10101010101\"," +
                                        "\"miljoe\": \"q2\"," +
                                        "\"status\": \"OK\"," +
                                        "\"eier\": \"Dolly\"," +
                                        "\"servicebehov\": false," +
                                        "\"automatiskInnsendingAvMeldekort\": false," +
                                        "\"aap115\": false," +
                                        "\"aap\": false}]," +
                                        "\"antallSider\": 2" +
                                        "}");
                    case "/api/v1/bruker?filter-eier=Dolly&filter-miljoe=q2&filter-personident=10101010101&page=1":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "\"arbeidsokerList\": [{" +
                                        "\"personident\": \"10101010101\"," +
                                        "\"miljoe\": \"q2\"," +
                                        "\"status\": \"ERROR\"," +
                                        "\"eier\": \"Dolly\"," +
                                        "\"servicebehov\": true," +
                                        "\"automatiskInnsendingAvMeldekort\": true," +
                                        "\"aap115\": true," +
                                        "\"aap\": true}]," +
                                        "\"antallSider\": 2" +
                                        "}");
                    case "/api/v1/bruker?filter-personident=10101010101":
                    case "/api/v1/bruker?filter-personident=10101010101&page=0":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "\"arbeidsokerList\": [{" +
                                        "\"personident\": \"10101010101\"," +
                                        "\"miljoe\": \"q2\"," +
                                        "\"status\": \"OK\"," +
                                        "\"eier\": \"Dolly\"," +
                                        "\"servicebehov\": false," +
                                        "\"automatiskInnsendingAvMeldekort\": false," +
                                        "\"aap115\": false," +
                                        "\"aap\": false},{" +
                                        "\"personident\": \"10101010101\"," +
                                        "\"miljoe\": \"t1\"," +
                                        "\"status\": \"OK\"," +
                                        "\"eier\": \"Orkestratoren\"," +
                                        "\"servicebehov\": false," +
                                        "\"automatiskInnsendingAvMeldekort\": true," +
                                        "\"aap115\": false," +
                                        "\"aap\": true" +
                                        "}]," +
                                        "\"antallSider\": 2" +
                                        "}");
                    case "/api/v1/bruker?filter-personident=10101010101&page=1":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "\"arbeidsokerList\": [{" +
                                        "\"personident\": \"10101010101\"," +
                                        "\"miljoe\": \"q0\"," +
                                        "\"status\": \"ERROR\"," +
                                        "\"eier\": \"Dolly\"," +
                                        "\"servicebehov\": false," +
                                        "\"automatiskInnsendingAvMeldekort\": false," +
                                        "\"aap115\": false," +
                                        "\"aap\": false}]," +
                                        "\"antallSider\": 2" +
                                        "}");
                    case "/api/v1/bruker?filter-personident=20202020202":
                    case "/api/v1/bruker?filter-personident=20202020202&page=0":
                        return new MockResponse().setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody("{" +
                                        "\"arbeidsokerList\": [{" +
                                        "\"personident\": \"20202020202\"," +
                                        "\"miljoe\": \"q1\"," +
                                        "\"status\": \"ERROR\"," +
                                        "\"eier\": \"Dolly\"," +
                                        "\"servicebehov\": false," +
                                        "\"automatiskInnsendingAvMeldekort\": false," +
                                        "\"aap115\": false," +
                                        "\"aap\": false}]," +
                                        "\"antallSider\": 1" +
                                        "}");


                }
                return new MockResponse().setResponseCode(404);
            }
        };
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}
