package no.nav.dolly.bestilling.instdata;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.bestilling.instdata.domain.MiljoerResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstdataClientTest {

    private static final String IDENT = "11111111111";
    private static final String ENVIRONMENT = "q2";
    private static final Long BESTILLING_ID = 123L;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private InstdataConsumer instdataConsumer;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private BestillingRepository bestillingRepository;

    @Spy
    private JsonMapper jsonMapper = JsonMapper.builder().build();

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private InstdataClient instdataClient;

    @Test
    void gjenopprettUtenInstdata_TomRetur() {

        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        StepVerifier.create(instdataClient.gjenopprett(new RsDollyBestillingRequest(), dollyPerson,
                                new BestillingProgress(), false))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldOppretteInstdataNaarOppholdIkkeFinnesFraFoer() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of("q2"))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(InstdataDTO.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.getInstdata(IDENT, ENVIRONMENT)).thenReturn(Mono.just(InstdataResponse.builder()
                .institusjonsopphold(Map.of())
                .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.empty());
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(InstdataResponse.builder()
                .status(HttpStatus.OK)
                .institusjonsopphold(Map.of("q2", List.of()))
                .build()));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton("q2"));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getLast(), is(equalTo("INST2_STATUS#q2:oppholdId=1$OK")));
                })
                .verifyComplete();

        verify(instdataConsumer).postInstdata(
                List.of(InstdataDTO.builder().norskident(IDENT).build()), ENVIRONMENT);
        verify(transaksjonMappingRepository, never()).save(any(TransaksjonMapping.class));
        verifyNoInteractions(bestillingRepository);
    }

    @Test
    void shouldLagreEksisterendeOppholdIderUtenAaPostePaaNytt() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of("q2"))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(InstdataDTO.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.getInstdata(IDENT, ENVIRONMENT)).thenReturn(Mono.just(InstdataResponse.builder()
                .institusjonsopphold(Map.of(ENVIRONMENT, List.of(
                        InstdataDTO.builder().norskident(IDENT).oppholdId("42").build(),
                        InstdataDTO.builder().norskident(IDENT).oppholdId("43").build())))
                .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.empty());
        when(transaksjonMappingRepository.save(any(TransaksjonMapping.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton("q2"));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getLast(), is(equalTo("INST2_STATUS#q2:oppholdId=1$OK")));
                })
                .verifyComplete();

        verify(instdataConsumer, never()).postInstdata(anyList(), anyString());
        var mappingCaptor = ArgumentCaptor.forClass(TransaksjonMapping.class);
        verify(transaksjonMappingRepository).save(mappingCaptor.capture());
        var mapping = mappingCaptor.getValue();
        assertThat(mapping.getBestillingId(), is(equalTo(BESTILLING_ID)));
        assertThat(mapping.getIdent(), is(equalTo(IDENT)));
        assertThat(mapping.getSystem(), is(equalTo("INST2")));
        assertThat(mapping.getMiljoe(), is(equalTo(ENVIRONMENT)));
        assertThat(jsonMapper.readValue(mapping.getTransaksjonId(), String[].class),
                arrayContainingInAnyOrder("42", "43"));
    }

    @Test
    void shouldIkkeKalleGetInstdataNaarMapperReturnererTomListe() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of("q2"))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of());
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton("q2"));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, false))
                .assertNext(_ -> {
                    verify(instdataConsumer, never()).getInstdata(anyString(), anyString());
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getLast(), is(equalTo("INST2_STATUS#q2:oppholdId=1$OK")));
                })
                .verifyComplete();
    }

    @Test
    void shouldLagreOgRapportereOppholdIdFraHverRespons() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of(ENVIRONMENT))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(InstdataDTO.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(
                InstdataResponse.builder()
                        .status(HttpStatus.OK)
                        .institusjonsopphold(Map.of(ENVIRONMENT, List.of(InstdataDTO.builder().oppholdId("42").build())))
                        .build(),
                InstdataResponse.builder()
                        .status(HttpStatus.OK)
                        .institusjonsopphold(Map.of(ENVIRONMENT, List.of(InstdataDTO.builder().oppholdId("43").build())))
                        .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.empty());
        when(transaksjonMappingRepository.save(any(TransaksjonMapping.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, true))
                .expectNextCount(1)
                .verifyComplete();

        var mappingCaptor = ArgumentCaptor.forClass(TransaksjonMapping.class);
        verify(transaksjonMappingRepository).save(mappingCaptor.capture());
        assertThat(mappingCaptor.getValue().getBestillingId(), is(equalTo(BESTILLING_ID)));
        assertThat(mappingCaptor.getValue().getIdent(), is(equalTo(IDENT)));
        assertThat(mappingCaptor.getValue().getSystem(), is(equalTo("INST2")));
        assertThat(mappingCaptor.getValue().getMiljoe(), is(equalTo(ENVIRONMENT)));
        assertThat(jsonMapper.readValue(mappingCaptor.getValue().getTransaksjonId(), String[].class),
                arrayContainingInAnyOrder("42", "43"));
        verify(transactionHelperService, times(2))
                .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast(),
                is(equalTo("INST2_STATUS#q2:oppholdId=42$OK,q2:oppholdId=43$OK")));
    }

    @Test
    void shouldRapportereFeilMedStandardOppholdIdNaarResponsManglerOpphold() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();
        var feilmelding = "Oppholdets startdato er utenfor perioden institusjonen var i drift";

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of(ENVIRONMENT))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(
                InstdataDTO.builder().norskident(IDENT).build()));
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(
                InstdataResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .feilmelding(feilmelding)
                        .build()));
        when(errorStatusDecoder.getErrorText(HttpStatus.BAD_REQUEST, feilmelding)).thenReturn(feilmelding);
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, true))
                .expectNextCount(1)
                .verifyComplete();

        verify(transactionHelperService, times(2))
                .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast(),
                is(equalTo("INST2_STATUS#q2:oppholdId=1$" + feilmelding)));
        verify(transaksjonMappingRepository, never()).save(any(TransaksjonMapping.class));
    }

    @Test
    void shouldIkkePostePaaNyttNaarOppholdErOpprettetTidligereIBestillingen() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();
        var mapping = TransaksjonMapping.builder()
                .bestillingId(BESTILLING_ID)
                .system("INST2")
                .ident(IDENT)
                .miljoe(ENVIRONMENT)
                .transaksjonId("[\"42\"]")
                .build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of(ENVIRONMENT))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(InstdataDTO.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.getInstdata(IDENT, ENVIRONMENT)).thenReturn(Mono.just(InstdataResponse.builder()
                .institusjonsopphold(Map.of(ENVIRONMENT, List.of(InstdataDTO.builder().oppholdId("42").build())))
                .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.just(mapping));
        when(transaksjonMappingRepository.save(any(TransaksjonMapping.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, false))
                .expectNextCount(1)
                .verifyComplete();

        verify(instdataConsumer, never()).postInstdata(anyList(), anyString());
        var mappingCaptor = ArgumentCaptor.forClass(TransaksjonMapping.class);
        verify(transaksjonMappingRepository).save(mappingCaptor.capture());
        assertThat(mappingCaptor.getValue(), is(sameInstance(mapping)));
        assertThat(mappingCaptor.getValue().getTransaksjonId(), is(equalTo("[\"42\"]")));
    }

    @Test
    void shouldGjenoppretteManglendeTidligereOppholdFoerEndring() {

        var currentOpphold = InstdataDTO.builder().norskident(IDENT).oppholdId("42").build();
        var request = stubEndring(currentOpphold);
        var tidligereBestilling = tidligereBestilling(100L);
        var tidligereKriterier = jsonMapper.readValue(
                tidligereBestilling.getBestKriterier(), RsDollyBestillingRequest.class);
        var eksisterendeOpphold = InstdataDTO.builder()
                .norskident(IDENT).institusjonsnummer("eksisterende").build();
        var manglendeOpphold = InstdataDTO.builder()
                .norskident(IDENT).institusjonsnummer("manglende").build();
        var gjenoppretting = TestPublisher.<InstdataResponse>create();

        when(bestillingRepository.findBestillingerByIdent(IDENT)).thenReturn(Flux.just(tidligereBestilling));
        when(transaksjonMappingRepository.findAllBySystemAndIdent("INST2", IDENT)).thenReturn(Flux.just(
                TransaksjonMapping.builder().bestillingId(BESTILLING_ID).miljoe(ENVIRONMENT).build(),
                TransaksjonMapping.builder().bestillingId(99L).miljoe("q1").build()));
        when(mapperFacade.mapAsList(eq(tidligereKriterier.getInstdata()), eq(InstdataDTO.class), any(MappingContext.class)))
                .thenReturn(List.of(eksisterendeOpphold, manglendeOpphold));
        when(instdataConsumer.getInstdata(IDENT, ENVIRONMENT)).thenReturn(Mono.just(InstdataResponse.builder()
                .institusjonsopphold(Map.of(ENVIRONMENT, List.of(eksisterendeOpphold)))
                .build()));
        when(instdataConsumer.postInstdata(List.of(manglendeOpphold), ENVIRONMENT)).thenReturn(gjenoppretting.flux());

        StepVerifier.create(instdataClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                        new BestillingProgress(), true))
                .then(() -> {
                    verify(instdataConsumer).postInstdata(List.of(manglendeOpphold), ENVIRONMENT);
                    verify(instdataConsumer, never()).postInstdata(List.of(currentOpphold), ENVIRONMENT);
                    gjenoppretting.emit(InstdataResponse.builder()
                            .status(HttpStatus.OK)
                            .institusjonsopphold(Map.of(ENVIRONMENT,
                                    List.of(InstdataDTO.builder().oppholdId("41").build())))
                            .build());
                })
                .expectNextCount(1)
                .verifyComplete();

        verify(instdataConsumer).postInstdata(List.of(currentOpphold), ENVIRONMENT);
        var mappingCaptor = ArgumentCaptor.forClass(TransaksjonMapping.class);
        verify(transaksjonMappingRepository, times(2)).save(mappingCaptor.capture());
        var restoredMapping = mappingCaptor.getAllValues().getFirst();
        assertThat(restoredMapping.getBestillingId(), is(equalTo(BESTILLING_ID)));
        assertThat(restoredMapping.getIdent(), is(equalTo(IDENT)));
        assertThat(restoredMapping.getMiljoe(), is(equalTo(ENVIRONMENT)));
        assertThat(restoredMapping.getSystem(), is(equalTo("INST2")));
        assertThat(restoredMapping.getTransaksjonId(), is(equalTo("[\"41\"]")));
    }

    @Test
    void shouldIkkeGjenoppretteTidligereBestillingMedMappingIMiljoe() {

        var currentOpphold = InstdataDTO.builder().norskident(IDENT).oppholdId("42").build();
        var request = stubEndring(currentOpphold);
        when(bestillingRepository.findBestillingerByIdent(IDENT)).thenReturn(Flux.just(tidligereBestilling(100L)));
        when(transaksjonMappingRepository.findAllBySystemAndIdent("INST2", IDENT)).thenReturn(Flux.just(
                TransaksjonMapping.builder().bestillingId(100L).miljoe(ENVIRONMENT).build()));

        StepVerifier.create(instdataClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                        new BestillingProgress(), true))
                .expectNextCount(1)
                .verifyComplete();

        verify(instdataConsumer, never()).getInstdata(anyString(), anyString());
        verify(instdataConsumer).postInstdata(List.of(currentOpphold), ENVIRONMENT);
        verify(instdataConsumer, times(1)).postInstdata(anyList(), anyString());
    }

    @ParameterizedTest
    @MethodSource("bestillingerSomIkkeSkalGjenopprettes")
    void shouldHoppeOverBestillingerSomIkkeSkalGjenopprettes(Bestilling tidligereBestilling) {

        var currentOpphold = InstdataDTO.builder().norskident(IDENT).oppholdId("42").build();
        var request = stubEndring(currentOpphold);
        when(bestillingRepository.findBestillingerByIdent(IDENT)).thenReturn(Flux.just(tidligereBestilling));

        StepVerifier.create(instdataClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                        new BestillingProgress(), true))
                .expectNextCount(1)
                .verifyComplete();

        verify(transaksjonMappingRepository, never()).findAllBySystemAndIdent(anyString(), anyString());
        verify(instdataConsumer, never()).getInstdata(anyString(), anyString());
        verify(instdataConsumer).postInstdata(List.of(currentOpphold), ENVIRONMENT);
        verify(instdataConsumer, times(1)).postInstdata(anyList(), anyString());
    }

    private RsDollyBestillingRequest stubEndring(InstdataDTO opphold) {

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().oppholdId(opphold.getOppholdId()).build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of(ENVIRONMENT))
                .build()));
        when(mapperFacade.mapAsList(eq(request.getInstdata()), eq(InstdataDTO.class), any(MappingContext.class)))
                .thenReturn(List.of(opphold));
        when(instdataConsumer.postInstdata(List.of(opphold), ENVIRONMENT)).thenReturn(Flux.just(
                InstdataResponse.builder()
                        .status(HttpStatus.OK)
                        .institusjonsopphold(Map.of(ENVIRONMENT, List.of(opphold)))
                        .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.empty());
        when(transaksjonMappingRepository.save(any(TransaksjonMapping.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(transactionHelperService.persister(any(), any(), any(), any()))
                .thenReturn(Mono.just(new BestillingProgress()));
        return request;
    }

    private static Bestilling tidligereBestilling(Long id) {

        return Bestilling.builder()
                .id(id)
                .bestKriterier("{\"instdata\":[{\"institusjonstype\":\"AS\"}]}")
                .build();
    }

    private static Stream<Bestilling> bestillingerSomIkkeSkalGjenopprettes() {

        var opprettetFraId = tidligereBestilling(100L);
        opprettetFraId.setOpprettetFraId(99L);
        var gjenopprettetFraIdent = tidligereBestilling(100L);
        gjenopprettetFraIdent.setGjenopprettetFraIdent(IDENT);
        var opprettetFraGruppe = tidligereBestilling(100L);
        opprettetFraGruppe.setOpprettetFraGruppeId(99L);
        return Stream.of(tidligereBestilling(BESTILLING_ID), opprettetFraId, gjenopprettetFraIdent,
                opprettetFraGruppe, Bestilling.builder().id(100L).bestKriterier("{}").build());
    }
}
