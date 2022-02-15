package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import no.nav.registre.aareg.consumer.rs.command.PostSyntAaregCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class SyntAaregConsumerTest {

    private SyntAaregConsumer syntAaregConsumer;

    private MockWebServer mockWebServer;

    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";


    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        Dispatcher dispatcher = getDispatcher();
        mockWebServer.setDispatcher(dispatcher);
        this.syntAaregConsumer = new SyntAaregConsumer(2, mockWebServer.url("/synt-aareg").toString());
    }


    @Test
    public void shouldGetSyntetiserteMeldinger() {
        var fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        var result = syntAaregConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

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
        String fnr3 = "03030303030";
        var fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2, fnr3));

        var result = syntAaregConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        var identsInResponse = result.stream().map(a -> a.getArbeidsforhold().getArbeidstaker().getIdent()).collect(Collectors.toList());

        assertThat(identsInResponse, hasItems(fnrs.get(0), fnrs.get(1), fnrs.get(2)));
    }

    @Test
    public void shouldLogOnEmptyResponse() {
        var fnrs = new ArrayList<>(Collections.singletonList(fnr1));

        var logger = (Logger) LoggerFactory.getLogger(PostSyntAaregCommand.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        syntAaregConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        assertThat(listAppender.list.size(), is(equalTo(1)));
        assertThat(listAppender.list.get(0).toString(), containsString("Feil under syntetisering"));
    }

    private Dispatcher getDispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/synt-aareg/api/v1/generate_aareg") && request.getMethod().equals("POST")) {
                    var body = request.getBody().readUtf8();
                    switch (body) {
                        case "[\"01010101010\",\"02020202020\"]":
                            return new MockResponse().setResponseCode(200)
                                    .addHeader("Content-Type", "application/json")
                                    .setBody(getResourceFileContent("arbeidsforholdsmelding.json"));
                        case "[\"03030303030\"]":
                            return new MockResponse().setResponseCode(200)
                                    .addHeader("Content-Type", "application/json")
                                    .setBody(getResourceFileContent("arbeidsforholdsmelding_paged.json"));
                        case "[\"01010101010\"]":
                            return new MockResponse().setResponseCode(500);
                    }
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
