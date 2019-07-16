package no.nav.registre.arena.core.consumer.rs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.arena.core.config.AppConfig;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.domain.Aap;
import no.nav.registre.arena.domain.Aap115;
import no.nav.registre.arena.domain.NyBruker;
import no.nav.registre.arena.domain.UtenServicebehov;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.registre.arena.core.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;


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

    private List<NyBruker> nyeBrukere;
    private NyBruker bruker1, bruker2;


    public ArenaForvalterConsumerTest() {
        bruker1 = NyBruker.builder()
                .personident("10101010101")
                .miljoe(miljoe)
                .utenServicebehov(
                        UtenServicebehov.builder()
                                .stansDato("2001-03-18").build())
                .automatiskInnsendingAvMeldekort(true)
                .aap115(Collections.singletonList(
                         Aap115.builder()
                                .fraDato("1998-07-02").build()
                ))
                .aap(Collections.singletonList(
                        Aap.builder()
                                .fraDato("1996-11-13")
                                .tilDato("2002-05-24").build()))
                .build();
        bruker2 = NyBruker.builder()
                .personident("20202020202")
                .miljoe(miljoe)
                .utenServicebehov(
                        UtenServicebehov.builder()
                                .stansDato("2015-08-20").build())
                .automatiskInnsendingAvMeldekort(true)
                .aap115(Collections.singletonList(
                         Aap115.builder()
                                .fraDato("2004-09-27").build()
                ))
                .aap(Collections.singletonList(
                        Aap.builder()
                                .fraDato("2008-02-28")
                                .tilDato("2009-05-01").build()))
                .build();

        nyeBrukere = Arrays.asList(bruker1, bruker2);

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
    public void checkEmptyListOnBadSentTilArenaForvalterRequest() {
        stubArenaForvalterBadRequest();

        List<Arbeidsoker> response = arenaForvalterConsumer.sendTilArenaForvalter(null);

       assertThat(response, is(Collections.EMPTY_LIST));
    }

    @Test
    public void emptyHentBrukereReturnsEmptyList() {
        stubArenaForvlaterEmptyHentBrukere();

        List<Arbeidsoker> response = arenaForvalterConsumer.hentBrukere();

        assertThat(response, is(Collections.EMPTY_LIST));
    }

    @Test
    public void hentBrukereTest() {
        stubArenaForvalterHentBrukereNoPage();
        stubArenaForvalterHentBrukereFirstPage();
        stubArenaForvalterHentBrukereSecondPage();

        List<Arbeidsoker> response = arenaForvalterConsumer.hentBrukere();

        assertThat(response.get(2).getPersonident(), is("08125949828"));
        assertThat(response.size(), is(156));
    }

    @Test
    public void breakOnNullBodyAfterFirstPage() {
        stubArenaForvalterHentBrukereNoPage();
        stubArenaForvalterHentBrukereNoBody();

        List<Arbeidsoker> response = arenaForvalterConsumer.hentBrukere();
        assertThat(response, is(Collections.EMPTY_LIST));
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
        List<Arbeidsoker> sfafr = arenaForvalterConsumer.sendTilArenaForvalter(Collections.EMPTY_LIST);

        assertThat(sfafr, is(Collections.EMPTY_LIST));
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


    private void stubArenaForvalterHentBrukereNoPage() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arenaForvalterenGetRequestResponseBody.json"))));
    }
    private void stubArenaForvalterHentBrukereFirstPage() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arenaForvalterenGetRequestResponseBody.json"))));
    }
    private void stubArenaForvalterHentBrukereSecondPage() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=2"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arenaForvalterenGetRequestResponseBodyPage2.json"))));
    }
    private void stubArenaForvalterHentBrukereNoBody() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));
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
