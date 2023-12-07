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
import no.nav.dolly.service.RsTransaksjonMapping;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TpsPersonServiceTest {

    private static final String AP = "PEN_AP";
    private static final String UT = "PEN_UT";
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
        ReflectionTestUtils.setField(tpsPersonService, "awaitMaxMillies", 1000);
    }

    @Test
    void syncPerson_TPS_OK() {

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

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(null), progress)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q1:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q2:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(1), containsString("q1:OK"));
                    assertThat(statusCaptor.getAllValues().get(1), containsString("q2:OK"));
                    assertThat(progress.getIsTpsSyncEnv(), contains("q1", "q2"));
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "PEN_AP",
            "PEN_UT"})
    void syncPerson_pensjon_TPS_OK(String pensjonType) {

        when(transaksjonMappingService.getTransaksjonMapping(getDollyPerson().getIdent()))
                .thenReturn(List.of(
                        RsTransaksjonMapping.builder().ident(IDENT).system(pensjonType).miljoe("q1").build(),
                        RsTransaksjonMapping.builder().ident(IDENT).system(pensjonType).miljoe("q2").build()));

        var progress = getProgress();

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(pensjonType), progress)
                        .map(ClientFuture::get))
                .expectNextCount(0)
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "PEN_AP,q1",
            "PEN_AP,q2",
            "PEN_UT,q1",
            "PEN_UT,q2"})
    void syncPerson_pensjon_med_en_TPS_OK(String pensjonType, String miljoe) {

        when(personServiceConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                .person(PdlPerson.Person.builder()
                                        .build())
                                .build()))
                        .build())
                .build()));
        when(tpsMessagingConsumer.getPerson(eq(IDENT), anyList())).thenReturn(Flux.just(
                PersonMiljoeDTO.builder().miljoe(miljoe).ident(IDENT).status("OK").build()));

        var progress = getProgress();

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(pensjonType, miljoe), progress)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), containsString(miljoe + ":Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(1), containsString(miljoe + ":OK"));
                    assertThat(progress.getIsTpsSyncEnv(), contains(miljoe));
                })
                .verifyComplete();
    }

    @Test
    void syncPerson_TPS_svarer_ikke() {

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

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(null), progress)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, Mockito.atLeastOnce())
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q1:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q2:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(statusCaptor.getAllValues().size() - 1), containsString("q1:FEIL= Synkronisering mot TPS gitt opp"));
                    assertThat(statusCaptor.getAllValues().get(statusCaptor.getAllValues().size() - 1), containsString("q2:FEIL= Synkronisering mot TPS gitt opp"));
                    assertThat(progress.getIsTpsSyncEnv(), is(empty()));
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "q1,q2",
            "q2,q1"})
    void syncPerson_en_TPS_svarer_ikke(String miljoeSomSvarer, String miljoeSomIkkeSvarer) {

        when(personServiceConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                .person(PdlPerson.Person.builder()
                                        .build())
                                .build()))
                        .build())
                .build()));
        when(tpsMessagingConsumer.getPerson(IDENT, ENVS.stream().toList())).thenReturn(Flux.just(
                PersonMiljoeDTO.builder().miljoe(miljoeSomSvarer).ident(IDENT).status("OK").build()));

        var progress = getProgress();

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(null), progress)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, Mockito.atLeastOnce())
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q1:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q2:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(statusCaptor.getAllValues().size() - 1), containsString(miljoeSomSvarer + ":OK"));
                    assertThat(statusCaptor.getAllValues().get(statusCaptor.getAllValues().size() - 1), containsString(miljoeSomIkkeSvarer + ":FEIL= Synkronisering mot TPS gitt opp"));
                    assertThat(progress.getIsTpsSyncEnv(), contains(miljoeSomSvarer));
                    assertThat(progress.getIsTpsSyncEnv(), contains(not(miljoeSomIkkeSvarer)));
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "q1,q2",
            "q2,q1"})
    void syncPerson_en_TPS_har_ikke_data(String miljoeSomHarData, String miljoeSomIkkeHarData) {

        when(personServiceConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                .person(PdlPerson.Person.builder()
                                        .build())
                                .build()))
                        .build())
                .build()));
        when(tpsMessagingConsumer.getPerson(IDENT, ENVS.stream().toList())).thenReturn(Flux.just(
                PersonMiljoeDTO.builder().miljoe(miljoeSomHarData).ident(IDENT).status("OK").build(),
                PersonMiljoeDTO.builder().miljoe(miljoeSomIkkeHarData).ident(IDENT).status("FEIL").utfyllendeMelding("Personen finnes ikke").build()));

        var progress = getProgress();

        StepVerifier.create(tpsPersonService.syncPerson(getDollyPerson(), getBestilling(null), progress)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, Mockito.atLeastOnce())
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q1:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(0), containsString("q2:Info: Synkronisering mot TPS startet ..."));
                    assertThat(statusCaptor.getAllValues().get(statusCaptor.getAllValues().size() - 1), containsString(miljoeSomHarData + ":OK"));
                    assertThat(statusCaptor.getAllValues().get(statusCaptor.getAllValues().size() - 1), containsString(miljoeSomIkkeHarData + ":FEIL= Personen finnes ikke"));
                    assertThat(progress.getIsTpsSyncEnv(), contains(miljoeSomHarData));
                    assertThat(progress.getIsTpsSyncEnv(), contains(not(miljoeSomIkkeHarData)));
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

    private RsDollyUtvidetBestilling getBestilling(String pensjon) {

        return getBestilling(pensjon, ENVS);
    }

    private RsDollyUtvidetBestilling getBestilling(String pensjon, String miljoe) {

        return getBestilling(pensjon, Set.of(miljoe));
    }

    private RsDollyUtvidetBestilling getBestilling(String pensjon, Set<String> miljoe) {

        var dollyBestilling = new RsDollyUtvidetBestilling();
        dollyBestilling.setEnvironments(miljoe);
        dollyBestilling.setPensjonforvalter(new PensjonData());

        if (AP.equals(pensjon)) {
            dollyBestilling.getPensjonforvalter()
                    .setAlderspensjon(new PensjonData.Alderspensjon());

        } else if (UT.equals(pensjon)) {
            dollyBestilling.getPensjonforvalter()
                    .setUforetrygd(new PensjonData.Uforetrygd());

        } else {
            dollyBestilling.getPensjonforvalter()
                    .setInntekt(new PensjonData.PoppInntekt());
        }

        return dollyBestilling;
    }
}