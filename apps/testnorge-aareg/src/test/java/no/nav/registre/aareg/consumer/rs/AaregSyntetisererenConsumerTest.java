package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@RestClientTest(AaregSyntetisererenConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AaregSyntetisererenConsumerTest {

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${syntrest.rest.api.url}")
    private String serverUrl;

    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private final String fnr3 = "03030303030";

    @BeforeEach
    public void setUp() {
        serverUrl += "/v1/generate/aareg";
    }

    @Test
    public void shouldGetSyntetiserteMeldinger() {
        var fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        stubAaregSyntetisererenConsumer();

        var result = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        assertThat(result.get(0).getArbeidsforhold().getArbeidstaker().getIdent(), equalTo(fnrs.get(0)));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getArbeidstidsordning(), equalTo("doegnkontinuerligSkiftOgTurnus355"));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getAvtaltArbeidstimerPerUke(), equalTo(35.5));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getEndringsdatoStillingsprosent(), equalTo(LocalDate.of(1985, 8, 1)));
        assertThat(result.get(0).getArbeidsforhold().getArbeidsavtale().getStillingsprosent(), equalTo(0.01));
        assertThat(result.get(0).getArbeidsforhold().getAnsettelsesPeriode().getFom(), equalTo(LocalDate.of(1985, 8, 1)));
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
        var fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2, fnr3));

        stubAaregSyntetisererenConsumerWithPaging();

        var result = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        var identsInResponse = result.stream().map(a -> a.getArbeidsforhold().getArbeidstaker().getIdent()).collect(Collectors.toList());

        assertThat(identsInResponse, hasItems(fnrs.get(0), fnrs.get(1), fnrs.get(2)));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        var fnrs = new ArrayList<>(Collections.singletonList(fnr1));

        var logger = (Logger) LoggerFactory.getLogger(AaregSyntetisererenConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        stubAaregSyntetisererenConsumerWithEmptyBody();

        aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Kunne ikke hente response body fra synthdata-aareg: NullPointerException"));
    }

    private void stubAaregSyntetisererenConsumer() {
        server.expect(requestToUriTemplate(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("[\"" + fnr1 + "\", \"" + fnr2 + "\"]"))
                .andRespond(withSuccess(getResourceFileContent("arbeidsforholdsmelding.json"), MediaType.APPLICATION_JSON));
    }

    private void stubAaregSyntetisererenConsumerWithPaging() {
        server.expect(requestToUriTemplate(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("[\"" + fnr1 + "\", \"" + fnr2 + "\"]"))
                .andRespond(withSuccess(getResourceFileContent("arbeidsforholdsmelding.json"), MediaType.APPLICATION_JSON));

        server.expect(requestToUriTemplate(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("[\"" + fnr3 + "\"]"))
                .andRespond(withSuccess(getResourceFileContent("arbeidsforholdsmelding_paged.json"), MediaType.APPLICATION_JSON));
    }

    private void stubAaregSyntetisererenConsumerWithEmptyBody() {
        server.expect(requestToUriTemplate(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("[\"" + fnr1 + "\"]"))
                .andRespond(withSuccess());
    }
}
