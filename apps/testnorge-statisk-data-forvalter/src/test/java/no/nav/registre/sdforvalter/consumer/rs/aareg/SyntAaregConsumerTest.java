package no.nav.registre.sdforvalter.consumer.rs.aareg;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.sdforvalter.consumer.rs.aareg.command.PostSyntAaregCommand;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static no.nav.registre.sdforvalter.ResourceUtils.getResourceFileContent;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class SyntAaregConsumerTest {

    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private SyntAaregConsumer syntAaregConsumer;
    private MockWebServer mockWebServer;
    private WebClient webClient;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        Dispatcher dispatcher = getDispatcher();
        mockWebServer.setDispatcher(dispatcher);
        syntAaregConsumer = new SyntAaregConsumer(webClient, 2, mockWebServer.url("/synt-aareg").toString());
    }

    @Test
    public void shouldGetSyntetiserteMeldinger() {
        var fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        var result = syntAaregConsumer.getSyntetiserteArbeidsforholdsmeldinger(fnrs);

        assertThat(result.getFirst().getArbeidsforhold().getArbeidstaker().getIdent(), equalTo(fnrs.getFirst()));
        assertThat(result.getFirst().getArbeidsforhold().getArbeidsavtale().getArbeidstidsordning(), equalTo("doegnkontinuerligSkiftOgTurnus355"));
        assertThat(result.getFirst().getArbeidsforhold().getArbeidsavtale().getAvtaltArbeidstimerPerUke(), equalTo(35.5));
        assertThat(result.getFirst().getArbeidsforhold().getArbeidsavtale().getEndringsdatoStillingsprosent(), equalTo(LocalDate.of(1985, 8, 1)));
        assertThat(result.getFirst().getArbeidsforhold().getArbeidsavtale().getStillingsprosent(), equalTo(0.01));
        assertThat(result.getFirst().getArbeidsforhold().getAnsettelsesPeriode().getFom(), equalTo(LocalDate.of(1985, 8, 1)));
        assertThat(result.getFirst().getArbeidsforhold().getPermisjon().getFirst().getPermisjonsId(), equalTo("a1b2c3"));
        assertThat(result.getFirst().getArbeidsforhold().getPermisjon().getFirst().getPermisjonsprosent(), equalTo(10.5));
        assertThat(result.getFirst().getArbeidsforhold().getUtenlandsopphold().getFirst().getLand(), equalTo("NOR"));
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

        var identsInResponse = result.stream().map(a -> a.getArbeidsforhold().getArbeidstaker().getIdent()).toList();

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
        assertThat(listAppender.list.getFirst().toString(), containsString("Feil under syntetisering"));
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private Dispatcher getDispatcher() {

        return new Dispatcher() {

            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) {

                if ("/synt-aareg/api/v1/generate_aareg".equals(request.getPath()) && "POST".equals(request.getMethod())) {
                    return switch (request.getBody().readUtf8()) {
                        case "[\"01010101010\",\"02020202020\"]" -> new MockResponse()
                                .setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody(getResourceFileContent("files/arbeidsforholdsmelding.json"));
                        case "[\"03030303030\"]" -> new MockResponse()
                                .setResponseCode(200)
                                .addHeader("Content-Type", "application/json")
                                .setBody(getResourceFileContent("files/arbeidsforholdsmelding_paged.json"));
                        case "[\"01010101010\"]" -> new MockResponse().setResponseCode(500);
                        default ->
                                throw new IllegalStateException("Unexpected request body: " + request.getBody().readUtf8());
                    };
                }
                return new MockResponse().setResponseCode(404);

            }

        };

    }
}
