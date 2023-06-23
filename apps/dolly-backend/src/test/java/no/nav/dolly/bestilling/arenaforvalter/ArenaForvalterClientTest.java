package no.nav.dolly.bestilling.arenaforvalter;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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
import reactor.test.StepVerifier;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse.BrukerFeilstatus.DUPLIKAT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaForvalterClientTest {

    private static final String IDENT = "12423353112";
    private static final String ENV = "q2";

    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private ArenaForvalterClient arenaForvalterClient;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AccessToken accessToken;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @BeforeEach
    void setup() {
        statusCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void gjenopprett_Ok() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));
        when(mapperFacade.map(any(Arenadata.class), eq(ArenaNyBruker.class))).thenReturn(ArenaNyBruker.builder()
                .kvalifiseringsgruppe(ArenaKvalifiseringsgruppe.IKVAL)
                .build());
        when(arenaForvalterConsumer.postArenaBruker(any(ArenaNyeBrukere.class)))
                .thenReturn(Flux.just(
                        ArenaNyeBrukereResponse.builder()
                                .status(HttpStatus.OK)
                                .arbeidsokerList(singletonList(ArenaBruker.builder()
                                        .miljoe(ENV)
                                        .status("OK")
                                        .build()))
                                .build()));
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));
        when(arenaForvalterConsumer.getBruker(anyString(), anyString()))
                .thenReturn(Flux.just(ArenaArbeidssokerBruker.builder()
                        .status(HttpStatus.OK)
                        .build()));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton(ENV));
        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                                .build(), progress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), Matchers.is(equalTo("q2$Info= Oppretting startet mot Arena ...")));
                    assertThat(statusCaptor.getAllValues().get(1), Matchers.is(equalTo("q2$OK")));
                })
                .verifyComplete();

        verify(arenaForvalterConsumer).getEnvironments();
        verify(arenaForvalterConsumer).postArenaBruker(any(ArenaNyeBrukere.class));
    }

    @Test
    void gjenopprett_FunksjonellFeil() {

        var progress = new BestillingProgress();
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));
        when(mapperFacade.map(any(Arenadata.class), eq(ArenaNyBruker.class))).thenReturn(ArenaNyBruker.builder()
                .kvalifiseringsgruppe(ArenaKvalifiseringsgruppe.IKVAL)
                .build());
        when(arenaForvalterConsumer.postArenaBruker(any(ArenaNyeBrukere.class)))
                .thenReturn(Flux.just(
                        ArenaNyeBrukereResponse.builder()
                                .status(HttpStatus.OK)
                                .nyBrukerFeilList(singletonList(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                        .miljoe(ENV)
                                        .nyBrukerFeilstatus(DUPLIKAT)
                                        .melding("message: 555 User Defined Resource Error")
                                        .build()))
                                .build()));

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton(ENV));
        when(arenaForvalterConsumer.getBruker(anyString(), anyString()))
                .thenReturn(Flux.just(ArenaArbeidssokerBruker.builder()
                        .status(HttpStatus.OK)
                        .build()));

        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                                .build(), progress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), Matchers.is(equalTo("q2$Info= Oppretting startet mot Arena ...")));
                    assertThat(statusCaptor.getAllValues().get(1), Matchers.is(equalTo("q2$DUPLIKAT: message= 555 User Defined Resource Error")));
                })
                .verifyComplete();

        verify(arenaForvalterConsumer).getEnvironments();
        verify(arenaForvalterConsumer).postArenaBruker(any(ArenaNyeBrukere.class));
    }

    @Test
    void gjenopprett_TekniskFeil() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton(ENV));
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));

        var gjenopprett = arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT)
                .build(), progress, false);

        Assertions.assertThrows(NullPointerException.class, () -> gjenopprett .blockFirst());
    }

    @Test
    void gjenopprett_EnvironmentForArenaNotSelected() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singleton("t3"));
        when(arenaForvalterConsumer.getEnvironments()).thenReturn(Flux.just(ENV));

        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                                progress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
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