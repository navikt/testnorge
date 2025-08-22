package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestgruppeServiceTest {

    private static final String BRUKERID = "123";
    private static final long GROUP_ID = 1L;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BrukerServiceConsumer brukerServiceConsumer;

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private PersonService personService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private TestgruppeService testgruppeService;

    @Test
    void opprettTestgruppe_HappyPath() {

        var rsTestgruppe = RsOpprettEndreTestgruppe.builder()
                .hensikt("hensikt")
                .navn("navn")
                .build();
        var bruker = Bruker.builder()
                .id(123L)
                .brukerId(BRUKERID)
                .build();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(new Testgruppe()));

        StepVerifier.create(testgruppeService.opprettTestgruppe(rsTestgruppe))
                .assertNext(testgruppe -> {
                    assertThat(testgruppe, is(notNullValue()));

                    var testgruppeCaptor = ArgumentCaptor.forClass(Testgruppe.class);
                    verify(testgruppeRepository).save(testgruppeCaptor.capture());
                    assertThat(testgruppeCaptor.getValue().getOpprettetAv(), is(bruker));
                    assertThat(testgruppeCaptor.getValue().getOpprettetAvId(), is(bruker.getId()));
                    assertThat(testgruppeCaptor.getValue().getSistEndretAv(), is(bruker));
                    assertThat(testgruppeCaptor.getValue().getSistEndretAvId(), is(bruker.getId()));
                    assertThat(testgruppeCaptor.getValue().getHensikt(), is(rsTestgruppe.getHensikt()));
                    assertThat(testgruppeCaptor.getValue().getNavn(), is(rsTestgruppe.getNavn()));
                })
                .verifyComplete();
    }

    @Test
    void fetchTestgruppeById_KasterExceptionHvisGruppeIkkeErFunnet() {

        when(testgruppeRepository.findById(GROUP_ID)).thenReturn(Mono.empty());

        StepVerifier.create(testgruppeService.fetchTestgruppeById(GROUP_ID))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void fetchTestgruppeById_returnererGruppeHvisGruppeMedIdFinnes() {

        var testgruppe = Testgruppe.builder().id(GROUP_ID).build();
        when(testgruppeRepository.findById(GROUP_ID)).thenReturn(Mono.just(testgruppe));

        StepVerifier.create(testgruppeService.fetchTestgruppeById(GROUP_ID))
                .assertNext(gruppe -> assertThat(gruppe, is(testgruppe)))
                .verifyComplete();
    }

    @Test
    void slettGruppeById_deleteBlirKaltMotRepoMedGittId() {

        var testgruppe = Testgruppe.builder().id(GROUP_ID).build();
        when(testgruppeRepository.findById(GROUP_ID)).thenReturn(Mono.just(testgruppe));
        when(transaksjonMappingRepository.deleteByGruppeId(GROUP_ID)).thenReturn(Mono.empty());
        when(bestillingService.slettBestillingerByGruppeId(GROUP_ID)).thenReturn(Mono.empty());
        when(identRepository.findByGruppeId(GROUP_ID, Pageable.unpaged()))
                .thenReturn(Flux.just(new Testident()));
        when(identRepository.deleteByGruppeId(GROUP_ID)).thenReturn(Mono.empty());
        when(brukerRepository.deleteBrukerFavoritterByGroupId(GROUP_ID)).thenReturn(Mono.empty());
        when(testgruppeRepository.deleteById(GROUP_ID)).thenReturn(Mono.empty());
        when(personService.recyclePersoner(any())).thenReturn(Mono.empty());
        when(brukerRepository.deleteBrukerFavoritterByGroupId(GROUP_ID)).thenReturn(Mono.empty());

        StepVerifier.create(testgruppeService.deleteGruppeById(GROUP_ID))
                .expectNextCount(0)
                .verifyComplete();

        verify(testgruppeRepository).findById(GROUP_ID);
        verify(transaksjonMappingRepository).deleteByGruppeId(GROUP_ID);
        verify(bestillingService).slettBestillingerByGruppeId(GROUP_ID);
        verify(identRepository).findByGruppeId(GROUP_ID, Pageable.unpaged());
        verify(identRepository).deleteByGruppeId(GROUP_ID);
        verify(brukerRepository).deleteBrukerFavoritterByGroupId(GROUP_ID);
        verify(personService).recyclePersoner(any());
        verify(testgruppeRepository).deleteById(GROUP_ID);
        verify(brukerRepository).deleteBrukerFavoritterByGroupId(GROUP_ID);
    }

    @Test
    void saveGrupper_kasterExceptionHvisDBConstraintErBrutt() {

        when(testgruppeRepository.save(any(Testgruppe.class))).thenThrow(DataIntegrityViolationException.class);

        StepVerifier.create(testgruppeService.saveGrupper(new HashSet<>(singletonList(new Testgruppe()))))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    void oppdaterTestgruppe_sjekkAtDBKalles() {

        var rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder().hensikt("test").navn("navn").build();
        var testgruppe = Testgruppe.builder()
                .id(GROUP_ID)
                .hensikt(rsOpprettEndreTestgruppe.getHensikt())
                .navn(rsOpprettEndreTestgruppe.getNavn())
                .build();
        var bruker = Bruker.builder().id(123L).brukerId(BRUKERID).build();

        when(testgruppeRepository.findById(GROUP_ID)).thenReturn(Mono.just(testgruppe));
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(testgruppe));
        StepVerifier.create(testgruppeService.oppdaterTestgruppe(GROUP_ID, rsOpprettEndreTestgruppe))
                .assertNext(testgruppe1 -> {
                    assertThat(testgruppe1, is(notNullValue()));

                    var testgruppeCaptor = ArgumentCaptor.forClass(Testgruppe.class);
                    verify(testgruppeRepository).save(testgruppeCaptor.capture());
                    assertThat(testgruppeCaptor.getValue().getSistEndretAv(), is(notNullValue()));
                    assertThat(testgruppeCaptor.getValue().getHensikt(), is(testgruppe.getHensikt()));
                    assertThat(testgruppeCaptor.getValue().getNavn(), is(testgruppe.getNavn()));
                    assertThat(testgruppeCaptor.getValue().getSistEndretAvId(), is(bruker.getId()));
                    assertThat(testgruppeCaptor.getValue().getSistEndretAv(), is(bruker));

                })
                .verifyComplete();
    }

    @Test
    void getTestgrupperAzureBruker_OK() {

        var bruker = Bruker.builder().id(123L).brukerId(BRUKERID).brukertype(Bruker.Brukertype.AZURE).build();
        var testgruppe = Testgruppe.builder()
                .id(GROUP_ID)
                .hensikt("test")
                .navn("navn")
                .build();

        when(brukerService.fetchOrCreateBruker(any())).thenReturn(Mono.just(bruker));
        when(testgruppeRepository.findByOpprettetAvId(any(), any())).thenReturn(Flux.just(testgruppe));
        when(testgruppeRepository.countByOpprettetAvId(bruker.getId())).thenReturn(Mono.just(1L));
        when(identRepository.countByGruppeId(testgruppe.getId())).thenReturn(Mono.just(1));
        when(identRepository.countByGruppeIdAndIBruk(testgruppe.getId(), true)).thenReturn(Mono.just(1));
        when(bestillingRepository.countByGruppeId(GROUP_ID)).thenReturn(Mono.just(1));
        when(mapperFacade.map(any(), eq(RsTestgruppe.class), any())).thenReturn(new RsTestgruppe());
        when(brukerRepository.findAll()).thenReturn(Flux.just(bruker));

        StepVerifier.create(testgruppeService.getTestgruppeByBrukerId(0, 10, null))
                .assertNext(testgruppe1 -> {
                    assertThat(testgruppe1, is(notNullValue()));
                    verify(testgruppeRepository).findByOpprettetAvId(bruker.getId(), Pageable.ofSize(10));

                    var contextArgumentCaptor = ArgumentCaptor.forClass(MappingContext.class);
                    verify(mapperFacade).map(any(), any(), contextArgumentCaptor.capture());
                    assertThat(contextArgumentCaptor.getValue().getProperty("bruker"), is(bruker));
                    assertThat(contextArgumentCaptor.getValue().getProperty("antallIdenter"), is(1));
                    assertThat(contextArgumentCaptor.getValue().getProperty("antallBestillinger"), is(1));
                    assertThat(contextArgumentCaptor.getValue().getProperty("antallIBruk"), is(1));
                })
                .verifyComplete();
    }

    @Test
    void getTestgrupperBankIdBruker_OK() {

        var bruker = Bruker.builder().id(123L).brukerId(BRUKERID).brukertype(Bruker.Brukertype.BANKID).build();
        var testgruppe = Testgruppe.builder()
                .id(GROUP_ID)
                .hensikt("test")
                .navn("navn")
                .build();

        when(brukerService.fetchOrCreateBruker(anyString())).thenReturn(Mono.just(bruker));
        when(testgruppeRepository.findByOpprettetAvId(any(), any())).thenReturn(Flux.just(testgruppe));
        when(testgruppeRepository.countByOpprettetAvId(bruker.getId())).thenReturn(Mono.just(1L));
        when(identRepository.countByGruppeId(testgruppe.getId())).thenReturn(Mono.just(1));
        when(identRepository.countByGruppeIdAndIBruk(testgruppe.getId(), true)).thenReturn(Mono.just(1));
        when(bestillingRepository.countByGruppeId(GROUP_ID)).thenReturn(Mono.just(1));
        when(mapperFacade.map(any(), eq(RsTestgruppe.class), any())).thenReturn(new RsTestgruppe());
        when(brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())).thenReturn(Mono.just(TilgangDTO.builder()
                .brukere(List.of(bruker.getBrukerId()))
                .build()));
        when(testgruppeRepository.findByOpprettetAv_BrukerIdIn(any(), any()))
                .thenReturn(Flux.just(testgruppe));
        when(testgruppeRepository.countByOpprettetAv_BrukerIdIn(any())).thenReturn(Mono.just(1L));
        when(brukerRepository.findAll()).thenReturn(Flux.just(bruker));

        StepVerifier.create(testgruppeService.getTestgruppeByBrukerId(0, 10, "123"))
                .assertNext(testgruppe1 -> {

                    assertThat(testgruppe1, is(notNullValue()));
                    verify(testgruppeRepository).findByOpprettetAv_BrukerIdIn(any(), any());
                    verify(testgruppeRepository).countByOpprettetAv_BrukerIdIn(any());

                    var contextArgumentCaptor = ArgumentCaptor.forClass(MappingContext.class);
                    verify(mapperFacade).map(any(), any(), contextArgumentCaptor.capture());
                    assertThat(contextArgumentCaptor.getValue().getProperty("bruker"), is(bruker));
                    assertThat(contextArgumentCaptor.getValue().getProperty("antallIdenter"), is(1));
                    assertThat(contextArgumentCaptor.getValue().getProperty("antallBestillinger"), is(1));
                    assertThat(contextArgumentCaptor.getValue().getProperty("antallIBruk"), is(1));
                })
                .verifyComplete();
    }
}