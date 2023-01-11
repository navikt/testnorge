package no.nav.dolly.bestilling.arenaforvalter;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse.BrukerFeilstatus.DUPLIKAT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private PdlPersonConsumer pdlPersonConsumer;

    @InjectMocks
    private ArenaForvalterClient arenaForvalterClient;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AccessToken accessToken;

    @Mock
    private PdlPersonBolk pdlPersonBolk;

    @Test
    void gjenopprett_Ok() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.getEnvironments(accessToken)).thenReturn(Flux.just(ENV));
        when(mapperFacade.map(any(Arenadata.class), eq(ArenaNyBruker.class))).thenReturn(ArenaNyBruker.builder()
                .kvalifiseringsgruppe(ArenaKvalifiseringsgruppe.IKVAL)
                .build());
        when(arenaForvalterConsumer.postArenadata(any(ArenaNyeBrukere.class), eq(accessToken)))
                .thenReturn(Flux.just(
                        ArenaNyeBrukereResponse.builder()
                                .arbeidsokerList(singletonList(ArenaNyeBrukereResponse.Bruker.builder()
                                        .miljoe(ENV)
                                        .status("OK")
                                        .build()))
                                .build()));
        when(arenaForvalterConsumer.deleteIdent(anyString(), anyString(), eq(accessToken))).thenReturn(Flux.just(""));
        when(arenaForvalterConsumer.getToken()).thenReturn(Mono.just(accessToken));
        when(arenaForvalterConsumer.getEnvironments(accessToken)).thenReturn(Flux.just(ENV));
        when(personServiceConsumer.getPdlSyncReady(anyString())).thenReturn(Mono.just(true));
        when(pdlPersonConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(pdlPersonBolk));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList(ENV));
        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                                .build(), progress, false)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .arenaforvalterStatus("q2$OK")
                        .build())
                .verifyComplete();

        verify(arenaForvalterConsumer).getEnvironments(accessToken);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaNyeBrukere.class), eq(accessToken));
    }

    @Test
    void gjenopprett_FunksjonellFeil() {

        var progress = new BestillingProgress();
        when(arenaForvalterConsumer.getEnvironments(accessToken)).thenReturn(Flux.just(ENV));
        when(mapperFacade.map(any(Arenadata.class), eq(ArenaNyBruker.class))).thenReturn(ArenaNyBruker.builder()
                .kvalifiseringsgruppe(ArenaKvalifiseringsgruppe.IKVAL)
                .build());
        when(arenaForvalterConsumer.postArenadata(any(ArenaNyeBrukere.class), eq(accessToken)))
                .thenReturn(Flux.just(
                        ArenaNyeBrukereResponse.builder()
                                .nyBrukerFeilList(singletonList(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                        .miljoe(ENV)
                                        .nyBrukerFeilstatus(DUPLIKAT)
                                        .melding("message: 555 User Defined Resource Error Lang feilmelding uegnet til å presenteres for bruker")
                                        .build()))
                                .build()));
        when(arenaForvalterConsumer.deleteIdent(anyString(), anyString(), eq(accessToken))).thenReturn(Flux.just(""));
        when(arenaForvalterConsumer.getToken()).thenReturn(Mono.just(accessToken));
        when(personServiceConsumer.getPdlSyncReady(anyString())).thenReturn(Mono.just(true));
        when(pdlPersonConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(pdlPersonBolk));

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList(ENV));

        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                                .build(), progress, false)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .arenaforvalterStatus("q2$Feil: DUPLIKAT. Se detaljer i logg. ")
                        .build())
                .verifyComplete();

        verify(arenaForvalterConsumer).getEnvironments(accessToken);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaNyeBrukere.class), eq(accessToken));
    }

    @Test
    void gjenopprett_TekniskFeil() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList(ENV));

        Assertions.assertThrows(NullPointerException.class, () ->
                arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                        .opprettetIPDL(true).build(), progress, false));
    }

    @Test
    void gjenopprett_EnvironmentForArenaNotSelected() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList("t3"));
        when(arenaForvalterConsumer.getToken()).thenReturn(Mono.just(accessToken));
        when(arenaForvalterConsumer.getEnvironments(accessToken)).thenReturn(Flux.just(ENV));
        when(personServiceConsumer.getPdlSyncReady(anyString())).thenReturn(Mono.just(true));
        when(pdlPersonConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(pdlPersonBolk));

        StepVerifier.create(arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(),
                                progress, false)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .arenaforvalterStatus("")
                        .build())
                .verifyComplete();
    }

    @Test
    void gjenopprett_ArenaForvalterNotIncluded() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setEnvironments(singletonList(ENV));
        arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                        .opprettetIPDL(true).build(), progress, false)
                .subscribe();

        verifyNoInteractions(arenaForvalterConsumer);
        assertThat(progress.getArenaforvalterStatus(), is(nullValue()));
    }
}