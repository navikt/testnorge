package no.nav.dolly.bestilling.tpsmessagingservice.service;

import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@ActiveProfiles("test")
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "classpath:application.yaml")
//@AutoConfigureWireMock(port = 0)
@ExtendWith(MockitoExtension.class)
class TpsPersonServiceTest {

    private static final Set<String> ENVS = Set.of("q1", "q2");
    private static final String IDENT = "11111111111";

    @Mock
    private TpsMessagingConsumer tpsMessagingConsumer;
    @Mock
    private TransactionHelperService transactionHelperService;
    @Mock
    private TransaksjonMappingService transaksjonMappingService;

    @Mock
    private PersonServiceConsumer personServiceConsumer;
    @InjectMocks
    private TpsPersonService tpsPersonService;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @BeforeEach
    void setup() {
        statusCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void syncPerson_TPS_OK() {

//        when(transaksjonMappingService.getTransaksjonMapping(eq(IDENT))).thenReturn(Collections.emptyList());
        when(personServiceConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                .person(PdlPerson.Person.builder()
                                        .build())
                                .build()))
                        .build())
                .build()));
        when(tpsMessagingConsumer.getPerson(IDENT, ENVS.stream().toList())).thenReturn(Flux.just(
                PersonMiljoeDTO.builder().miljoe("q1").ident(IDENT).status("OK").build(),
                PersonMiljoeDTO.builder().miljoe("q2").ident(IDENT).status("OK").build()));

        var progress = getProgress();

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(), progress)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q1:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q2:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(1), containsString("q1:OK"));
                    assertThat(statusCaptor.getAllValues().get(1), containsString("q2:OK"));
                    assertThat(progress.getIsTpsSyncEnv(), contains("q1","q2"));
                })
                .verifyComplete();
    }

    @Test
    void syncPerson_TPS_NOK() {

//        when(transaksjonMappingService.getTransaksjonMapping(eq(IDENT))).thenReturn(Collections.emptyList());
        when(personServiceConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                .person(PdlPerson.Person.builder()
                                        .build())
                                .build()))
                        .build())
                .build()));
        when(tpsMessagingConsumer.getPerson(IDENT, ENVS.stream().toList())).thenReturn(Flux.empty());

        var progress = getProgress();

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(), progress)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q1:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q2:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(1), containsString("q1:OK"));
                    assertThat(statusCaptor.getAllValues().get(1), containsString("q2:OK"));
                    assertThat(progress.getIsTpsSyncEnv(), contains("q1","q2"));
                })
                .verifyComplete();
    }

    private BestillingProgress getProgress() {

        return BestillingProgress.builder()
                .ident(IDENT)
                .build();
    }

    private DollyPerson getDollyPerson() {

        return DollyPerson.builder()
                .ident(IDENT)
                .build();
    }

    private RsDollyUtvidetBestilling getBestilling() {

        var dollyBestilling = new RsDollyUtvidetBestilling();
        dollyBestilling.setEnvironments(ENVS);
        dollyBestilling.setPensjonforvalter(
                PensjonData.builder()
                        .inntekt(new PensjonData.PoppInntekt())
                        .build());
        return dollyBestilling;
    }

    private void stubPostLagreTpYtelse(boolean withError) {

        if (!withError) {
            stubFor(get(urlPathMatching("(.*)/api/v1/personer/{ident}"))
                    .willReturn(ok()
                            .withBody("[{\"miljo\":\"q1\",\"ident\":\"" + IDENT + "\",\"status\":\"]")
                            .withHeader("Content-Type", "application/json")));
        } else {
            stubFor(post(urlPathMatching("(.*)/api/v1/tp/ytelse"))
                    .willReturn(ok()
                            .withBody("{\"status\":[{\"miljo\":\"tx\",\"response\":{\"httpStatus\":{\"status\":500,\"reasonPhrase\":\"Internal Server Error\"},\"message\":\"404 Not Found from POST https://tp-q4.dev.intern.nav.no/api/tjenestepensjon/08525803725/forhold/3200/ytelse\",\"path\":\"/api/v1/tp/ytelse\"}}]}")
                            .withHeader("Content-Type", "application/json")));
        }
    }
}