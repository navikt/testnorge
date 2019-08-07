package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.arena.core.config.AppConfig;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.domain.Aap;
import no.nav.registre.arena.domain.Aap115;
import no.nav.registre.arena.domain.NyBruker;
import no.nav.registre.arena.domain.UtenServicebehov;
import org.junit.Test;
import org.junit.runner.RunWith;

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
                .kvalifiseringsgruppe("IKVAL")
                .aap115(Collections.singletonList(
                         Aap115.builder()
                                .fraDato("1998-07-02").build()))
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
                .kvalifiseringsgruppe("IKVAL")
                .aap115(Collections.singletonList(
                         Aap115.builder()
                                .fraDato("2004-09-27").build()))
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

        List<Arbeidsoeker> arbeidsoekerList = arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);

        assertThat(arbeidsoekerList.size(), is(equalTo(2)));
        assertThat(arbeidsoekerList.get(1).getPersonident(), is("20202020202"));
        assertThat(arbeidsoekerList.get(0).getServicebehov(), is(false));

    }

    @Test(expected = HttpClientErrorException.class)
    public void checkEmptyListOnBadSentTilArenaForvalterRequest() {
        stubArenaForvalterBadRequest();

        List<Arbeidsoeker> response = arenaForvalterConsumer.sendTilArenaForvalter(null);

       assertThat(response, is(Collections.EMPTY_LIST));
    }

    @Test
    public void emptyHentBrukereReturnsEmptyList() {
        stubArenaForvlaterEmptyHentBrukere();

        List<Arbeidsoeker> response = arenaForvalterConsumer.hentArbeidsoekere();

        assertThat(response, is(Collections.EMPTY_LIST));
    }

    @Test
    public void hentBrukereTest() {
        stubArenaForvalterHentBrukereNoPage();
        stubArenaForvalterHentBrukereFirstPage();
        stubArenaForvalterHentBrukereSecondPage();

        List<Arbeidsoeker> response = arenaForvalterConsumer.hentArbeidsoekere();

        assertThat(response.get(2).getPersonident(), is("09038817873"));
        assertThat(response.size(), is(3));
    }

    @Test
    public void hentBrukere_breakOnNullBodyAfterFirstPage() {
        stubArenaForvalterHentBrukereNoPage();
        stubArenaForvalterHentBrukereNoBody();

        List<Arbeidsoeker> response = arenaForvalterConsumer.hentArbeidsoekere();
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
                        .withBody(
                                "{" +
                                "  \"arbeidsokerList\": [" +
                                "    {" +
                                "      \"personident\": \"07098524627\"," +
                                "      \"miljoe\": \"q2\"," +
                                "      \"status\": \"OK\"," +
                                "      \"eier\": \"Dolly\"," +
                                "      \"servicebehov\": false," +
                                "      \"automatiskInnsendingAvMeldekort\": false" +
                                "    }," +
                                "    {" +
                                "      \"personident\": \"13119316876\"," +
                                "      \"miljoe\": \"t4\"," +
                                "      \"status\": \"OK\"," +
                                "      \"eier\": \"Dolly\"," +
                                "      \"servicebehov\": true," +
                                "      \"automatiskInnsendingAvMeldekort\": true" +
                                "    }" +
                                "  ]," +
                                "  \"antallSider\": 2" +
                                "}"
                        )));
    }
    private void stubArenaForvalterHentBrukereFirstPage() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
                                "  \"arbeidsokerList\": [" +
                                "    {" +
                                "      \"personident\": \"07098524627\"," +
                                "      \"miljoe\": \"q2\"," +
                                "      \"status\": \"OK\"," +
                                "      \"eier\": \"Dolly\"," +
                                "      \"servicebehov\": false," +
                                "      \"automatiskInnsendingAvMeldekort\": false" +
                                "    }," +
                                "    {" +
                                "      \"personident\": \"13119316876\"," +
                                "      \"miljoe\": \"t4\"," +
                                "      \"status\": \"OK\"," +
                                "      \"eier\": \"Dolly\"," +
                                "      \"servicebehov\": true," +
                                "      \"automatiskInnsendingAvMeldekort\": true" +
                                "    }" +
                                "  ]," +
                                "  \"antallSider\": 2" +
                                "}"
                        )));
    }
    private void stubArenaForvalterHentBrukereSecondPage() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=2"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
                                 "  \"arbeidsokerList\": [" +
                                 "    {" +
                                 "      \"personident\": \"09038817873\"," +
                                 "      \"miljoe\": \"q1\"," +
                                 "      \"status\": \"OK\"," +
                                 "      \"eier\": \"Dolly\"," +
                                 "      \"servicebehov\": true," +
                                 "      \"automatiskInnsendingAvMeldekort\": true" +
                                 "    }" +
                                 "  ]," +
                                 "  \"antallSider\": 2" +
                                 "}"
                        )));
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
                .withRequestBody(equalToJson(
                        "{" +
                                "  \"nyeBrukere\": [" +
                                "    {" +
                                "      \"personident\": \"10101010101\"," +
                                "      \"miljoe\": \"q2\"," +
                                "      \"kvalifiseringsgruppe\": \"IKVAL\"," +                              
                                "      \"utenServicebehov\": {" +
                                "        \"stansDato\": \"2001-03-18\"" +
                                "      }," +
                                "      \"automatiskInnsendingAvMeldekort\": true," +
                                "      \"aap115\": [" +
                                "        {" +
                                "          \"fraDato\": \"1998-07-02\"" +
                                "        }" +
                                "      ]," +
                                "      \"aap\": [" +
                                "        {" +
                                "          \"fraDato\": \"1996-11-13\"," +
                                "          \"tilDato\": \"2002-05-24\"" +
                                "        }" +
                                "      ]" +
                                "    }," +
                                "    {" +
                                "      \"personident\": \"20202020202\"," +
                                "      \"miljoe\": \"q2\"," +
                                "      \"kvalifiseringsgruppe\": \"IKVAL\"," +
                                "      \"utenServicebehov\": {" +
                                "        \"stansDato\": \"2015-08-20\"" +
                                "      }," +
                                "      \"automatiskInnsendingAvMeldekort\": true," +
                                "      \"aap115\": [" +
                                "        {" +
                                "          \"fraDato\": \"2004-09-27\"" +
                                "        }" +
                                "      ]," +
                                "      \"aap\": [" +
                                "        {" +
                                "          \"fraDato\": \"2008-02-28\"," +
                                "          \"tilDato\": \"2009-05-01\"" +
                                "        }" +
                                "      ]" +
                                "    }" +
                                "  ]" +
                                "}"
                ))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
                                "  \"arbeidsokerList\": [" +
                                "    {" +
                                "      \"personident\": \"10101010101\"," +
                                "      \"miljoe\": \"q2\"," +
                                "      \"status\": \"ERROR\"," +
                                "      \"eier\": \"Dolly\"," +
                                "      \"servicebehov\": false," +
                                "      \"automatiskInnsendingAvMeldekort\": false" +
                                "    }," +
                                "    {" +
                                "      \"personident\": \"20202020202\"," +
                                "      \"miljoe\": \"q2\"," +
                                "      \"status\": \"ERROR\"," +
                                "      \"eier\": \"Dolly\"," +
                                "      \"servicebehov\": false," +
                                "      \"automatiskInnsendingAvMeldekort\": false" +
                                "    }" +
                                "  ]" +
                                "}"
                        )));
    }

    private void stubArenaForvalterBadRequest() {

        stubFor(post(urlEqualTo("/arena-forvalteren/api/v1/bruker?eier=" + EIER))
                .willReturn(aResponse()
                        .withStatus(400)));
    }

}
