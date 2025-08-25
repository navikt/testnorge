package no.nav.dolly.bestilling.arenaforvalter;

import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaAap115Service;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaAapService;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaBrukerService;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaDagpengerService;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaStansYtelseService;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaForvalterClientTest {

    private static final String IDENT = "12423353112";
    private static final String ENV = "q2";

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private ArenaBrukerService arenaBrukerService;

    @Mock
    private ArenaAap115Service arenaAap115Service;

    @Mock
    private ArenaAapService arenaAapService;

    @Mock
    private ArenaDagpengerService arenaDagpengerService;

    @Mock
    private ArenaStansYtelseService arenaStansYtelseService;

    @InjectMocks
    private ArenaForvalterClient arenaForvalterClient;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @BeforeEach
    void setup() {
        statusCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void gjenopprett_Ok() {

        when(applicationConfig.getClientTimeout()).thenReturn(30L);

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));
        when(arenaForvalterConsumer.getArenaBruker(anyString(), anyString()))
                .thenReturn(Mono.just(ArenaStatusResponse.builder()
                        .status(HttpStatus.OK)
                        .build()));
        when(arenaBrukerService.sendBruker(any(), any(), any(), any()))
                .thenReturn(Flux.just("Oppretting: OK"));
        when(arenaAap115Service.sendAap115(any(), any(), any(), any())).thenReturn(Flux.empty());
        when(arenaAapService.sendAap(any(), any(), any(), any())).thenReturn(Flux.empty());
        when(arenaDagpengerService.sendDagpenger(any(), any(), any(), any())).thenReturn(Flux.empty());
        when(arenaStansYtelseService.stopYtelse(any(), any(), any())).thenReturn(Flux.empty());
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton(ENV));
        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                                .build(), progress, false))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), Matchers.is(equalTo("q2$BRUKER= Info= Oppretting startet mot Arena ...")));
                    assertThat(statusCaptor.getAllValues().get(1), Matchers.is(equalTo("q2$BRUKER Oppretting= OK")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprett_FunksjonellFeil() {

        when(applicationConfig.getClientTimeout()).thenReturn(30L);

        var progress = new BestillingProgress();
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton(ENV));
        when(arenaForvalterConsumer.getArenaBruker(anyString(), anyString()))
                .thenReturn(Mono.just(ArenaStatusResponse.builder()
                        .status(HttpStatus.OK)
                        .build()));

        when(arenaBrukerService.sendBruker(any(), any(), any(), any()))
                .thenReturn(Flux.just("Oppretting: DUPLIKAT:message: 555 User Defined Resource Error"));
        when(arenaAap115Service.sendAap115(any(), any(), any(), any())).thenReturn(Flux.empty());
        when(arenaAapService.sendAap(any(), any(), any(), any())).thenReturn(Flux.empty());
        when(arenaDagpengerService.sendDagpenger(any(), any(), any(), any())).thenReturn(Flux.empty());
        when(arenaStansYtelseService.stopYtelse(any(), any(), any())).thenReturn(Flux.empty());
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                                .build(), progress, false))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), Matchers.is(equalTo("q2$BRUKER= Info= Oppretting startet mot Arena ...")));
                    assertThat(statusCaptor.getAllValues().get(1), Matchers.is(equalTo("q2$BRUKER Oppretting= DUPLIKAT=message= 555 User Defined Resource Error")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprett_TekniskFeil() {

        when(applicationConfig.getClientTimeout()).thenReturn(30L);

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton(ENV));
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                                .build(), progress, false))
                .assertNext(status -> assertThat(status.getArenaforvalterStatus(), is(nullValue())))
                .verifyComplete();
    }

    @Test
    void gjenopprett_EnvironmentForArenaNotSelected() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton("t3"));
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                                progress, false))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), Matchers.is(equalTo("")));
                    assertThat(statusCaptor.getAllValues().get(1), Matchers.is(equalTo("")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprett_ArenaForvalterNotIncluded() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setEnvironments(singleton(ENV));
        arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                        .build(), progress, false)
                .subscribe();

        verifyNoInteractions(arenaForvalterConsumer);
        assertThat(progress.getArenaforvalterStatus(), is(nullValue()));
    }
}