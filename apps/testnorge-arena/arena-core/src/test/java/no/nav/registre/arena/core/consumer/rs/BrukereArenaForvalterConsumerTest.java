package no.nav.registre.arena.core.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import no.nav.registre.arena.core.consumer.rs.util.ArbeidssoekerCacheUtil;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.UtenServicebehov;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.core.config.AppConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {BrukereArenaForvalterConsumer.class, AppConfig.class, ArbeidssoekerCacheUtil.class})
@EnableAutoConfiguration
public class BrukereArenaForvalterConsumerTest {

    @Autowired
    private BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;

    private String miljoe = "q2", EIER = "ORKESTRATOREN";

    private NyBruker bruker1, bruker2;

    public BrukereArenaForvalterConsumerTest() {
        bruker1 = NyBruker.builder()
                .personident("10101010101")
                .miljoe(miljoe)
                .utenServicebehov(
                        UtenServicebehov.builder()
                                .stansDato(LocalDate.of(2001, 3, 18)).build())
                .automatiskInnsendingAvMeldekort(true)
                .kvalifiseringsgruppe(Kvalifiseringsgrupper.IKVAL)
                .build();
        bruker2 = NyBruker.builder()
                .personident("20202020202")
                .miljoe(miljoe)
                .utenServicebehov(
                        UtenServicebehov.builder()
                                .stansDato(LocalDate.of(2015, 8, 20)).build())
                .automatiskInnsendingAvMeldekort(true)
                .kvalifiseringsgruppe(Kvalifiseringsgrupper.IKVAL)
                .build();
    }

    @Test(expected = HttpClientErrorException.class)
    public void checkEmptyListOnBadSentTilArenaForvalterRequest() {
        stubArenaForvalterBadRequest();

        NyeBrukereResponse response = brukereArenaForvalterConsumer.sendTilArenaForvalter(null);

        assertThat(response.getArbeidsoekerList(), is(Collections.EMPTY_LIST));
    }

    @Test
    public void hentBrukereTest() {
        stubArenaForvalterHentBrukereNoPage();
        stubArenaForvalterHentBrukereFirstPage();
        stubArenaForvalterHentBrukereSecondPage();

        List<Arbeidsoeker> response = brukereArenaForvalterConsumer.hentArbeidsoekere("", "", "");

        assertThat(response.get(2).getPersonident(), is("09038817873"));
        assertThat(response.size(), is(3));
    }

    @Test
    public void slettBrukereTest() {
        stubArenaForvalterSlettBrukere();

        Boolean test = brukereArenaForvalterConsumer.slettBruker("10101010101", "q2");
        assertThat(test, is(true));
    }

    @Test(expected = HttpClientErrorException.class)
    public void slettBrukereBadReq() {
        stubArenaForvalterSlettBrukereBadReq();

        Boolean test = brukereArenaForvalterConsumer.slettBruker("10101010101", "q2");
        assertThat(test, is(false));
    }

    @Test
    public void getBrukereFilterTest() {
        stubArenaForvalterHentBrukereFilter();
        stubArenaForvalterHentBrukereFilterPageEn();
        stubArenaForvalterHentBrukereFilterPageTo();

        List<Arbeidsoeker> response = brukereArenaForvalterConsumer.hentArbeidsoekere("10101010101", "Dolly", "q2");

        assertThat(response.get(0).getStatus(), is("OK"));
        assertThat(response.size(), is(2));
        assertThat(response.get(1).getAap(), is(true));
    }

    @Test
    public void getBrukereFilterPersonident() {
        stubArenaForvalterFilterPersonident();
        stubArenaForvalterFilterPersonidentPageEn();
        stubArenaForvalterFilterPersonidentPageTo();
        stubArenaForvalterFilterPersonidentGuyTo();
        stubArenaForvalterFilterPersonidentGuyToPageEn();

        List<Arbeidsoeker> response = brukereArenaForvalterConsumer.hentArbeidsoekere("10101010101", "", "");
        response.addAll(brukereArenaForvalterConsumer.hentArbeidsoekere("20202020202", "", ""));

        assertThat(response.size(), is(4));
        assertThat(response.get(2).getPersonident(), is("10101010101"));
        assertThat(response.get(2).getMiljoe(), is("q0"));
        assertThat(response.get(1).getPersonident(), is("10101010101"));
        assertThat(response.get(3).getPersonident(), is("20202020202"));
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

    private void stubArenaForvalterFilterPersonident() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-personident=10101010101"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterFilterPersonidentPageEn() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-personident=10101010101&page=0"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterFilterPersonidentPageTo() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-personident=10101010101&page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterFilterPersonidentGuyTo() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-personident=20202020202"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterFilterPersonidentGuyToPageEn() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-personident=20202020202&page=0"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterHentBrukereFilter() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-miljoe=q2&filter-eier=Dolly&filter-personident=10101010101"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterHentBrukereFilterPageEn() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-miljoe=q2&filter-eier=Dolly&filter-personident=10101010101&page=0"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterHentBrukereFilterPageTo() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?filter-miljoe=q2&filter-eier=Dolly&filter-personident=10101010101&page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{" +
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
                                        "}"
                        )));
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterHentBrukereFirstPage() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=0"))
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
                                        "}"
                        )));
    }

    private void stubArenaForvalterHentBrukereSecondPage() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=1"))
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
                                        "      \"automatiskInnsendingAvMeldekort\": true," +
                                        "      \"aap115\": true," +
                                        "      \"aap\": true" +
                                        "    }" +
                                        "  ]," +
                                        "  \"antallSider\": 2" +
                                        "}"
                        )));
    }

    private void stubArenaForvalterHentBrukereNoBody() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?page=0"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));
    }

    private void stubArenaForvlaterEmptyHentBrukere() {
        stubFor(get(urlEqualTo("/arena-forvalteren/api/v1/bruker?"))
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
                                        "      \"automatiskInnsendingAvMeldekort\": false," +
                                        "      \"aap115\": false," +
                                        "      \"aap\": false" +
                                        "    }," +
                                        "    {" +
                                        "      \"personident\": \"20202020202\"," +
                                        "      \"miljoe\": \"q2\"," +
                                        "      \"status\": \"ERROR\"," +
                                        "      \"eier\": \"Dolly\"," +
                                        "      \"servicebehov\": false," +
                                        "      \"automatiskInnsendingAvMeldekort\": false," +
                                        "      \"aap115\": false," +
                                        "      \"aap\": false" +
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
