package no.nav.dolly.bestilling.arenaforvalter;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse.BrukerFeilstatus.DUPLIKAT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private ArenaForvalterClient arenaForvalterClient;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AccessToken accessToken;

    @BeforeEach
    void setup() {

//        when(arenaForvalterConsumer.getToken()).thenReturn(Mono.just(accessToken));
//        when(arenaForvalterConsumer.deleteIdent(anyString(), anyString(), eq(accessToken))).thenReturn(Flux.just(""));
    }

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

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList(ENV));
        arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                .opprettetIPDL(true).build(), progress, false);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo("q2$OK")));
        verify(arenaForvalterConsumer).getEnvironments(accessToken);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaNyeBrukere.class), eq(accessToken));
    }

    @Test
    public void gjenopprett_FunksjonellFeil() {

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

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList(ENV));
        arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                .opprettetIPDL(true).build(), progress, false);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo("q2$Feil: DUPLIKAT. Se detaljer i logg. ")));
        verify(arenaForvalterConsumer).getEnvironments(accessToken);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaNyeBrukere.class), eq(accessToken));
    }

    @Test
    public void gjenopprett_TekniskFeil() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList(ENV));

        Assertions.assertThrows(NullPointerException.class, () ->
                arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                        .opprettetIPDL(true).build(), progress, false));
    }

    @Test
    public void gjenopprett_EnvironmentForArenaNotSelected() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setArenaforvalter(Arenadata.builder().build());
        request.setEnvironments(singletonList("t3"));
        when(arenaForvalterConsumer.getToken()).thenReturn(Mono.just(accessToken));
        when(arenaForvalterConsumer.getEnvironments(accessToken)).thenReturn(Flux.just(ENV));

        arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                .opprettetIPDL(true).build(), progress, false);

        assertThat(progress.getArenaforvalterStatus(), is(emptyString()));
    }

    @Test
    public void gjenopprett_ArenaForvalterNotIncluded() {

        var progress = new BestillingProgress();

        var request = new RsDollyBestillingRequest();
        request.setEnvironments(singletonList(ENV));
        arenaForvalterClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT)
                .opprettetIPDL(true).build(), progress, false);

        verifyNoInteractions(arenaForvalterConsumer);
        assertThat(progress.getArenaforvalterStatus(), is(nullValue()));
    }
}