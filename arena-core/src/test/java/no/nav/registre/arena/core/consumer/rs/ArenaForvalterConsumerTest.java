package no.nav.registre.arena.core.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.arena.core.config.AppConfig;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.domain.*;
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
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.registre.arena.core.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8082)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {ArenaForvalterConsumer.class, AppConfig.class})
@EnableAutoConfiguration
public class ArenaForvalterConsumerTest {

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    private String miljoe = "q2", EIER = "ORKESTRATOREN";

    private NyeBrukereList nyeBrukere;
    private ObjectMapper objectMapper = new ObjectMapper();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date testDate;


    public ArenaForvalterConsumerTest() throws JsonParseException, JsonMappingException, IOException, ParseException {
        String brukere = getResourceFileContent("arenaForvalterenNyeBrukere.json");
        this.nyeBrukere = objectMapper.readValue(brukere, NyeBrukereList.class);
        this.testDate = sdf.parse("2009-05-01");
        this.objectMapper.setDateFormat(sdf);
    }


    @Test
    public void sjekkObjectMapping() {

        assertThat(nyeBrukere.getNyeBrukere().size(), is(equalTo(2)));
        List<NyBruker> brukere = nyeBrukere.getNyeBrukere();
        assertThat(brukere.get(0).getPersonident(), is("10101010101"));
        assertThat(brukere.get(1).getAap().get(0).getTilDato(), is(testDate));
        assertThat(brukere.get(1).getAutomatiskInnsendingAvMeldekort(), is(true));

    }

    @Test
    public void statusEtterOpprettedeBrukereIArenaForvalter() {

        stubArenaForvalterConsumer();

        List<Arbeidsoker> arbeidsokerList = arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);

        assertThat(arbeidsokerList.size(), is(equalTo(2)));
        assertThat(arbeidsokerList.get(1).getPersonident(), is("20202020202"));
        assertThat(arbeidsokerList.get(0).getServicebehov(), is(false));

    }

    @Test(expected = HttpClientErrorException.class)
    public void shouldLogOnBadRequest() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(ArenaForvalterConsumer.class);
        logger.addAppender(listAppender);

        stubArenaForvalterBadRequest();

        List<Arbeidsoker> response = arenaForvalterConsumer.sendTilArenaForvalter(null);

        assertThat(listAppender.list.size(), is(Matchers.equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke opprette nye brukere. Status: "));

    }

    @Test
    public void logOnEmptyHentBrukere() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(ArenaForvalterConsumer.class);
        logger.addAppender(listAppender);

        stubArenaForvlaterEmptyHentBrukere();

        List<Arbeidsoker> response = arenaForvalterConsumer.hentBrukere();

        assertThat(listAppender.list.size(), is(Matchers.equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra Arena Forvalteren."));
    }

    @Test
    public void hentBrukereTest() {
        stubArenaForvalterHentBrukere();

        List<Arbeidsoker> response = arenaForvalterConsumer.hentBrukere();

        assertThat(response.get(2).getPersonident(), is("08125949828"));
        assertThat(response.size(), is(156));
    }

    @Test
    public void hentEksisterendeIdenterTest() {
        stubArenaForvalterHentBrukere();
        List<String> eksisterendeIdenter = arenaForvalterConsumer.hentEksisterendeIdenter();

        assertThat(eksisterendeIdenter.get(2), is("08125949828"));
        assertThat(eksisterendeIdenter.size(), is(156));
    }

    @Test
    public void slettBrukereTest() {
        stubArenaForvalterSlettBrukere();

        Boolean test = arenaForvalterConsumer.slettBrukerSuccessful("10101010101", "q2");
        assertThat(test, is(true));
    }

    @Test(expected = HttpClientErrorException.class)
    public void slettBrukereBadReq() {
        stubArenaForvalterSlettBrukereBadReq();

        Boolean test = arenaForvalterConsumer.slettBrukerSuccessful("10101010101", "q2");
        assertThat(test, is(false));
    }

    @Test(expected = HttpClientErrorException.class)
    public void badCreateJson() {
        // String test = arenaForvalterConsumer.createJsonRequestBody(null);
        List<Arbeidsoker> sfafr = arenaForvalterConsumer.sendTilArenaForvalter(null);

        assertThat(sfafr, is(nullValue()));
    }


    private void stubArenaForvalterSlettBrukereBadReq() {
        stubFor(delete(urlEqualTo("/arena-forvalteren/api/v1/bruker?miljoe=q2&personident=10101010101"))
                .willReturn(aResponse()
                .withStatus(400)
                .withBody(
                        "{" +
                                "\"timestamp\": \"2019-07-03T07:45:19.109+0000\"," +
                                "\"status\": 400," +
                                "\"error\": \"Bad Request\"," +
                                "\"message\": \"Identen er ikke registrert i arena-forvalteren\"," +
                                "\"path\": \"/api/v1/bruker\"" +
                        "}"
                )));
    }

    private void stubArenaForvalterSlettBrukere() {
        stubFor(delete(urlEqualTo("/arena-forvalteren/api/v1/bruker?miljoe=q2&personident=10101010101"))
                .willReturn(ok()));
    }

    private void stubArenaForvalterHentBrukere() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arenaForvalterenGetRequestResponseBody.json"))));
    }

    private void stubArenaForvlaterEmptyHentBrukere() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker"))
                .willReturn(ok().withHeader("Content-Type", "application/json")));
    }

    private void stubArenaForvalterConsumer() {
        stubFor(post(urlEqualTo("/arena-forvalteren/api/v1/bruker?eier=" + EIER))
                .withRequestBody(equalToJson(getResourceFileContent("arenaForvalterenNyeBrukere.json")))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arenaForvalterenNyeBrukereResponse.json"))));
    }

    private void stubArenaForvalterBadRequest() {

        stubFor(post(urlEqualTo("/arena-forvalteren/api/v1/bruker?eier=" + EIER))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody(getResourceFileContent("arenaForvalterenBadRequest.json"))));
    }

}
