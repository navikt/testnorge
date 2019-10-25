package no.nav.registre.aareg.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class AaregSyntetisererenConsumerTest {

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private final String fnr3 = "03030303030";

    @Test
    public void shouldGetSyntetiserteMeldinger() {
        List<String> fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        stubAaregSyntetisererenConsumer();

        List<RsAaregOpprettRequest> result = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        assertThat(result.get(0).getArbeidsforhold().getArbeidstaker().getIdent(), equalTo(fnrs.get(0)));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getArbeidstidsordning(), equalTo("doegnkontinuerligSkiftOgTurnus355"));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getAvtaltArbeidstimerPerUke(), equalTo(35.5));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getEndringsdatoStillingsprosent(), equalTo(LocalDateTime.of(1985, 8, 1, 0, 0, 0)));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getStillingsprosent(), equalTo(0.01));
        assertThat(result.get(0).getArbeidsforhold().getAnsettelsesPeriode().getFom(), equalTo(LocalDateTime.of(1985, 8, 1, 0, 0, 0)));
        assertThat(result.get(0).getArbeidsforhold().getPermisjon().get(0).getPermisjonsId(), equalTo("a1b2c3"));
        assertThat(result.get(0).getArbeidsforhold().getPermisjon().get(0).getPermisjonsprosent(), equalTo(10.5));
        assertThat(result.get(0).getArbeidsforhold().getUtenlandsopphold().get(0).getLand(), equalTo("NOR"));
        assertThat(result.get(1).getArbeidsforhold().getArbeidstaker().getIdent(), equalTo(fnrs.get(1)));
        assertThat(result.get(1).getArbeidsforhold().getArbeidsforholdID(), equalTo("oAq5SJgOPDHQnERi"));
        assertThat(result.get(1).getArbeidsforhold().getArbeidsforholdstype(), equalTo("ordinaertArbeidsforhold"));
        assertThat(result.get(1).getArbeidsforhold().getArbeidsavtale().getYrke(), equalTo("990458162"));
    }

    @Test
    public void shouldGetSyntetiserteMeldingerWithPaging() {
        List<String> fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2, fnr3));

        stubAaregSyntetisererenConsumerWithPaging();

        List<RsAaregOpprettRequest> result = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        assertThat(result.get(0).getArbeidsforhold().getArbeidstaker().getIdent(), equalTo(fnrs.get(0)));
        assertThat(result.get(1).getArbeidsforhold().getArbeidstaker().getIdent(), equalTo(fnrs.get(1)));
        assertThat(result.get(2).getArbeidsforhold().getArbeidstaker().getIdent(), equalTo(fnrs.get(2)));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        List<String> fnrs = new ArrayList<>(Collections.singletonList(fnr1));

        Logger logger = (Logger) LoggerFactory.getLogger(AaregSyntetisererenConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        stubAaregSyntetisererenConsumerWithEmptyBody();

        aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra synthdata-aareg: NullPointerException"));
    }

    private void stubAaregSyntetisererenConsumer() {
        stubFor(post(urlPathEqualTo("/synthdata-aareg/api/v1/generate/aareg"))
                .withRequestBody(equalToJson("[\"" + fnr1 + "\", \"" + fnr2 + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arbeidsforholdsmelding.json"))));
    }

    private void stubAaregSyntetisererenConsumerWithPaging() {
        stubFor(post(urlPathEqualTo("/synthdata-aareg/api/v1/generate/aareg"))
                .withRequestBody(equalToJson("[\"" + fnr1 + "\", \"" + fnr2 + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arbeidsforholdsmelding.json"))));

        stubFor(post(urlPathEqualTo("/synthdata-aareg/api/v1/generate/aareg"))
                .withRequestBody(equalToJson("[\"" + fnr3 + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("arbeidsforholdsmelding_paged.json"))));
    }

    private void stubAaregSyntetisererenConsumerWithEmptyBody() {
        stubFor(post(urlPathEqualTo("/synthdata-aareg/api/v1/generate/aareg"))
                .withRequestBody(equalToJson("[\"" + fnr1 + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}
