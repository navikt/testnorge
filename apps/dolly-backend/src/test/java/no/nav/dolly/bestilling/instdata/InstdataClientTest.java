package no.nav.dolly.bestilling.instdata;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.bestilling.instdata.domain.MiljoerResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
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
    private TransactionHelperService transactionHelperService;

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Spy
    private JsonMapper jsonMapper = JsonMapper.builder().build();

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private InstdataClient instdataClient;

    @Test
    void shouldSkipRequestWithoutInstdata() {

        var dollyPerson = DollyPerson.builder().ident(IDENT).build();
        var request = new RsDollyBestillingRequest();
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson,
                                new BestillingProgress()))
                .expectNextCount(0)
                .verifyComplete();

        verifyNoInteractions(mapperFacade, instdataConsumer, transactionHelperService, transaksjonMappingRepository);
    }

    @Test
    void shouldSkipRequestWithoutEnvironments() {

        var request = new RsDollyBestillingRequest();
        request.setInstdata(List.of(RsInstdata.builder().build()));

        StepVerifier.create(instdataClient.gjenopprett(request,
                        DollyPerson.builder().ident(IDENT).build(), new BestillingProgress()))
                .verifyComplete();

        verifyNoInteractions(mapperFacade, instdataConsumer, transactionHelperService, transaksjonMappingRepository);
    }

    @Test
    void shouldUseFallbackOppholdIdWhenSuccessfulResponseHasNoOpphold() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).isOpprett(true).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of("q2"))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(InstdataDTO.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(InstdataResponse.builder()
                .status(HttpStatus.OK)
                .institusjonsopphold(Map.of("q2", List.of()))
                .build()));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton("q2"));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getLast(), is(equalTo("INST2_STATUS#q2:oppholdId=1$OK")));
                })
                .verifyComplete();

        verifyNoInteractions(transaksjonMappingRepository);
    }

    @Test
    void shouldSaveMappingForExistingOppholdWithoutPostingAgain() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of("q2"))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(InstdataDTO.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.getInstdata(IDENT, ENVIRONMENT)).thenReturn(Mono.just(InstdataResponse.builder()
                .institusjonsopphold(Map.of(ENVIRONMENT,
                        List.of(InstdataDTO.builder().norskident(IDENT).oppholdId("42").build())))
                .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.empty());
        when(transaksjonMappingRepository.save(any(TransaksjonMapping.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton("q2"));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getLast(), is(equalTo("INST2_STATUS#q2:oppholdId=1$OK")));
                })
                .verifyComplete();

        verify(instdataConsumer, never()).postInstdata(anyList(), anyString());
        var mappingCaptor = ArgumentCaptor.forClass(TransaksjonMapping.class);
        verify(transaksjonMappingRepository).save(mappingCaptor.capture());
        assertThat(mappingCaptor.getValue().getBestillingId(), is(equalTo(BESTILLING_ID)));
        assertThat(mappingCaptor.getValue().getIdent(), is(equalTo(IDENT)));
        assertThat(mappingCaptor.getValue().getSystem(), is(equalTo("INST2")));
        assertThat(mappingCaptor.getValue().getMiljoe(), is(equalTo(ENVIRONMENT)));
        assertThat(jsonMapper.readValue(mappingCaptor.getValue().getTransaksjonId(), String[].class),
                arrayContainingInAnyOrder("42"));
    }

    @Test
    void shouldSkipInstdataLookupWhenMappedRequestIsEmpty() {

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

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress))
                .assertNext(_ -> {
                    verify(instdataConsumer, never()).getInstdata(anyString(), anyString());
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getLast(), is(equalTo("INST2_STATUS#q2:oppholdId=1$OK")));
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldLagreOppholdIdFraResponsPaaBestilling(boolean isLeggTil) {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).isOpprett(!isLeggTil).isLeggTil(isLeggTil).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of(ENVIRONMENT))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class))).thenReturn(List.of(InstdataDTO.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(InstdataResponse.builder()
                .status(HttpStatus.OK)
                .institusjonsopphold(Map.of(ENVIRONMENT, List.of(InstdataDTO.builder().oppholdId("42").build())))
                .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.empty());
        when(transaksjonMappingRepository.save(any(TransaksjonMapping.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress))
                .expectNextCount(1)
                .verifyComplete();

        var mappingCaptor = ArgumentCaptor.forClass(TransaksjonMapping.class);
        verify(transaksjonMappingRepository).save(mappingCaptor.capture());
        assertThat(mappingCaptor.getValue().getBestillingId(), is(equalTo(BESTILLING_ID)));
        assertThat(mappingCaptor.getValue().getIdent(), is(equalTo(IDENT)));
        assertThat(mappingCaptor.getValue().getSystem(), is(equalTo("INST2")));
        assertThat(mappingCaptor.getValue().getMiljoe(), is(equalTo(ENVIRONMENT)));
        assertThat(mappingCaptor.getValue().getTransaksjonId(), is(equalTo("[\"42\"]")));
        verify(transactionHelperService, times(2))
                .persister(eq(progress), any(), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast(), is(equalTo("INST2_STATUS#q2:oppholdId=42$OK")));
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
        when(transaksjonMappingRepository.save(mapping)).thenReturn(Mono.just(mapping));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress))
                .expectNextCount(1)
                .verifyComplete();

        verify(instdataConsumer, never()).postInstdata(anyList(), anyString());
        verify(transaksjonMappingRepository).save(mapping);
        assertThat(jsonMapper.readValue(mapping.getTransaksjonId(), String[].class),
                arrayContainingInAnyOrder("42"));
    }

    @Test
    void shouldUpdateExistingMappingWithIdsFromAllSuccessfulResponses() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).isOpprett(true).build();
        var mapping = TransaksjonMapping.builder()
                .id(1L)
                .bestillingId(BESTILLING_ID)
                .system("INST2")
                .ident(IDENT)
                .miljoe(ENVIRONMENT)
                .transaksjonId("[\"old-id\"]")
                .build();
        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of(ENVIRONMENT))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class)))
                .thenReturn(List.of(InstdataDTO.builder().norskident(IDENT).build()));
        when(instdataConsumer.postInstdata(anyList(), eq(ENVIRONMENT))).thenReturn(Flux.just(
                InstdataResponse.builder().status(HttpStatus.CREATED)
                        .institusjonsopphold(Map.of(ENVIRONMENT,
                                List.of(InstdataDTO.builder().oppholdId("42").build())))
                        .build(),
                InstdataResponse.builder().status(HttpStatus.OK)
                        .institusjonsopphold(Map.of(ENVIRONMENT,
                                List.of(InstdataDTO.builder().oppholdId("43").build())))
                        .build()));
        when(transaksjonMappingRepository.findAllByBestillingId(BESTILLING_ID)).thenReturn(Flux.just(mapping));
        when(transaksjonMappingRepository.save(mapping)).thenReturn(Mono.just(mapping));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));
        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress))
                .expectNext(progress)
                .verifyComplete();

        verify(transaksjonMappingRepository).save(mapping);
        assertThat(jsonMapper.readValue(mapping.getTransaksjonId(), String[].class),
                arrayContainingInAnyOrder("42", "43"));
        verify(transactionHelperService, times(2))
                .persister(eq(progress), any(), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast(),
                is(equalTo("INST2_STATUS#q2:oppholdId=42$OK,q2:oppholdId=43$OK")));
    }

    @Test
    void shouldPersistErrorWithoutSavingMappingWhenResponseHasNoOpphold() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).isOpprett(true).build();
        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(MiljoerResponse.builder()
                .institusjonsoppholdEnvironments(List.of(ENVIRONMENT))
                .build()));
        when(mapperFacade.mapAsList(anyList(), eq(InstdataDTO.class), any(MappingContext.class)))
                .thenReturn(List.of(InstdataDTO.builder().norskident(IDENT).build()));
        when(instdataConsumer.postInstdata(anyList(), eq(ENVIRONMENT))).thenReturn(Flux.just(
                InstdataResponse.builder().status(HttpStatus.BAD_REQUEST).feilmelding("Invalid stay").build()));
        when(errorStatusDecoder.getErrorText(HttpStatus.BAD_REQUEST, "Invalid stay"))
                .thenReturn("Feil= Invalid stay");
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(progress));
        var request = new RsDollyBestillingRequest();
        request.setId(BESTILLING_ID);
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton(ENVIRONMENT));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress))
                .expectNext(progress)
                .verifyComplete();

        verifyNoInteractions(transaksjonMappingRepository);
        verify(transactionHelperService, times(2))
                .persister(eq(progress), any(), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast(),
                is(equalTo("INST2_STATUS#q2:oppholdId=1$Feil= Invalid stay")));
    }
}
