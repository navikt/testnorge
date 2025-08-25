package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.BANKID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NavigasjonServiceTest {

    private static final String IDENT = "12345678901";
    private static final String TENOR_IDENT = "12845678901";
    private static final String TESTBRUKER = "123";

    @Mock
    private IdentRepository identRepository;

    @Mock
    private IdentService identService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private PdlDataConsumer pdlDataConsumer;

    @Mock
    private BrukerService brukerService;

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private BestillingRepository bestillingRepository;

    @InjectMocks
    private NavigasjonService navigasjonService;

    @Test
    void testNavigerTilIdentFound() {

        var expected = RsWhereAmI.builder()
                .identHovedperson(IDENT)
                .identNavigerTil(IDENT)
                .build();

        var testident = Testident.builder()
                .ident(IDENT)
                .gruppeId(1L)
                .build();

        var fullPersonDTO = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident(IDENT).build())
                .build();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder().brukerId(TESTBRUKER).build()));
        when(identRepository.findByIdent(any())).thenReturn(Mono.just(testident));
        when(identService.getPaginertIdentIndex(any(), any())).thenReturn(Mono.just(1));
        when(mapperFacade.map(any(), eq(RsTestgruppe.class), any())).thenReturn(new RsTestgruppe());
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fullPersonDTO));
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder().build()));
        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(Testgruppe.builder().id(1L).build()));
        when(identRepository.countByGruppeId(any())).thenReturn(Mono.just(1));
        when(bestillingRepository.countByGruppeId(any())).thenReturn(Mono.just(0));
        when(identRepository.countByGruppeIdAndIBruk(any(), any())).thenReturn(Mono.just(1));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().brukerId(TESTBRUKER).brukertype(AZURE).build()));

        StepVerifier.create(navigasjonService.navigerTilIdent(IDENT))
                .expectNextMatches(rsWhereAmI ->
                        rsWhereAmI.getIdentHovedperson().equals(expected.getIdentHovedperson()) &&
                                rsWhereAmI.getIdentNavigerTil().equals(expected.getIdentNavigerTil()))
                .verifyComplete();
    }

    @Test
    void testBankidUserNavigerTilIdentAllowed() {

        var expected = RsWhereAmI.builder()
                .identHovedperson(TENOR_IDENT)
                .identNavigerTil(TENOR_IDENT)
                .build();

        var tiAllowed = Testident.builder()
                .ident(TENOR_IDENT)
                .gruppeId(1L)
                .build();

        var fpAllowed = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident(TENOR_IDENT).build())
                .build();

        when(identRepository.findByIdent(TENOR_IDENT)).thenReturn(Mono.just(tiAllowed));
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fpAllowed));
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder()
                .brukerId(TESTBRUKER)
                .brukertype(BANKID)
                .build()));
        when(identService.getPaginertIdentIndex(any(), any())).thenReturn(Mono.just(1));
        when(mapperFacade.map(any(), eq(RsTestgruppe.class), any())).thenReturn(new RsTestgruppe());
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder().build()));
        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(Testgruppe.builder().id(1L).build()));
        when(identRepository.countByGruppeId(any())).thenReturn(Mono.just(1));
        when(bestillingRepository.countByGruppeId(any())).thenReturn(Mono.just(0));
        when(identRepository.countByGruppeIdAndIBruk(any(), any())).thenReturn(Mono.just(1));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().brukerId(TESTBRUKER).brukertype(AZURE).build()));

        StepVerifier.create(navigasjonService.navigerTilIdent(TENOR_IDENT))
                .expectNextMatches(rsWhereAmI ->
                        rsWhereAmI.getIdentHovedperson().equals(expected.getIdentHovedperson()) &&
                                rsWhereAmI.getIdentNavigerTil().equals(expected.getIdentNavigerTil()))
                .verifyComplete();
    }

    @Test
    void testBankidUserNavigerTilIdentRefused_GirIngenTreff() {

        var fpRefused = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident(IDENT).build())
                .build();
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder()
                .brukerId(TESTBRUKER)
                .brukertype(BANKID)
                .build()));
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fpRefused));
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder().build()));

        StepVerifier.create(navigasjonService.navigerTilIdent(IDENT))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testNavigerTilIdentNotFound() {

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder()
                .brukerId(TESTBRUKER)
                .brukertype(AZURE)
                .build()));
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.empty());
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.empty());

        StepVerifier.create(navigasjonService.navigerTilIdent(IDENT))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testNavigerTilBestillingFound() {

        var bestillingId = 1L;
        var bestilling = Bestilling.builder()
                .id(bestillingId)
                .gruppeId(1L)
                .build();

        when(bestillingService.fetchBestillingById(any())).thenReturn(Mono.just(bestilling));
        when(testgruppeRepository.findByBestillingId(bestilling.getId())).thenReturn(Mono.just(Testgruppe.builder().id(bestilling.getGruppeId()).build()));
        when(bestillingService.getPaginertBestillingIndex(bestillingId, bestilling.getGruppeId())).thenReturn(Mono.just(1));
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder().brukerId(TESTBRUKER).build()));
        when(identRepository.countByGruppeId(1L)).thenReturn(Mono.just(1));
        when(bestillingRepository.countByGruppeId(bestilling.getGruppeId())).thenReturn(Mono.just(1));
        when(identRepository.countByGruppeIdAndIBruk(bestilling.getGruppeId(), true)).thenReturn(Mono.just(1));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().brukerId(TESTBRUKER).brukertype(AZURE).build()));

        Mono<RsWhereAmI> result = navigasjonService.navigerTilBestilling(1L);

        StepVerifier.create(result)
                .expectNextMatches(rsWhereAmI -> rsWhereAmI.getBestillingNavigerTil().equals(1L))
                .verifyComplete();
    }

    @Test
    void testNavigerTilBestillingNotFound() {

        var bestillingId = 1L;
        when(bestillingService.fetchBestillingById(any())).thenReturn(Mono.empty());

        StepVerifier.create(navigasjonService.navigerTilBestilling(bestillingId))
                .expectError(NotFoundException.class)
                .verify();
    }
}