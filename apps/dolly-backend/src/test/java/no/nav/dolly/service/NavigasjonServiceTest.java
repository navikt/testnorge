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
import no.nav.dolly.repository.IdentRepository;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class NavigasjonServiceTest {

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

    @InjectMocks
    private NavigasjonService navigasjonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNavigerTilIdentFound() {
        String ident = "12445678901";
        RsWhereAmI expected = RsWhereAmI.builder()
                .identHovedperson(ident)
                .identNavigerTil(ident)
                .build();

        Testident testident = Testident.builder()
                .ident(ident)
                .testgruppe(
                        Testgruppe.builder()
                                .id(1L)
                                .build()
                ).build();

        FullPersonDTO fullPersonDTO = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident(ident).build())
                .build();

        when(identRepository.findByIdent(any())).thenReturn(Optional.of(testident));
        when(identService.getPaginertIdentIndex(any(), any())).thenReturn(Optional.of(1));
        when(mapperFacade.map(any(), eq(RsWhereAmI.class))).thenReturn(expected);
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fullPersonDTO));
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder().build()));

        Mono<RsWhereAmI> result = navigasjonService.navigerTilIdent(ident, Bruker.builder().brukertype(Bruker.Brukertype.AZURE).build());

        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testBankidUserNavigerTilIdentAllowed() {
        String identAllowed = "12881212345";

        RsWhereAmI expected = RsWhereAmI.builder()
                .identHovedperson(identAllowed)
                .identNavigerTil(identAllowed)
                .build();

        Testident tiAllowed = Testident.builder()
                .ident(identAllowed)
                .testgruppe(
                        Testgruppe.builder()
                                .id(1L)
                                .build()
                ).build();

        FullPersonDTO fpAllowed = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident(identAllowed).build())
                .build();

        when(identRepository.findByIdent(eq(identAllowed))).thenReturn(Optional.of(tiAllowed));

        when(identService.getPaginertIdentIndex(any(), any())).thenReturn(Optional.of(1));
        when(mapperFacade.map(any(), eq(RsWhereAmI.class))).thenReturn(RsWhereAmI.builder().identHovedperson(identAllowed).build());
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fpAllowed));
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder().build()));

        Mono<RsWhereAmI> result = navigasjonService.navigerTilIdent(identAllowed, Bruker.builder().brukertype(Bruker.Brukertype.BANKID).build());

        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void testBankidUserNavigerTilIdentRefused() {
        String identRefused = "12441212345";

        Testident tiRefused = Testident.builder()
                .ident(identRefused)
                .testgruppe(
                        Testgruppe.builder()
                                .id(1L)
                                .build()
                ).build();

        FullPersonDTO fpRefused = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident(identRefused).build())
                .build();

        when(identRepository.findByIdent(eq(identRefused))).thenReturn(Optional.of(tiRefused));

        when(identService.getPaginertIdentIndex(any(), any())).thenReturn(Optional.of(1));
        when(mapperFacade.map(any(), eq(RsWhereAmI.class))).thenReturn(RsWhereAmI.builder().identHovedperson(identRefused).build());
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fpRefused));
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder().build()));

        Mono<RsWhereAmI> result = navigasjonService.navigerTilIdent(identRefused, Bruker.builder().brukertype(Bruker.Brukertype.BANKID).build());

        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testNavigerTilIdentNotFound() {
        String ident = "12445678901";

        when(identRepository.findByIdent(any())).thenReturn(Optional.empty());
        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.empty());
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.empty());

        Mono<RsWhereAmI> result = navigasjonService.navigerTilIdent(ident, Bruker.builder().brukertype(Bruker.Brukertype.AZURE).build());

        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testNavigerTilBestillingFound() {
        Long bestillingId = 1L;

        Testgruppe testgruppe = new Testgruppe();

        Bestilling bestilling = new Bestilling();
        bestilling.setGruppe(testgruppe);

        when(bestillingService.fetchBestillingById(any())).thenReturn(bestilling);
        when(bestillingService.getPaginertBestillingIndex(any(), any())).thenReturn(Optional.of(1));
        when(mapperFacade.map(any(Testgruppe.class), eq(RsTestgruppe.class))).thenReturn(new RsTestgruppe());

        Mono<RsWhereAmI> result = navigasjonService.navigerTilBestilling(bestillingId);

        StepVerifier.create(result)
                .expectNextMatches(rsWhereAmI -> rsWhereAmI.getBestillingNavigerTil().equals(bestillingId))
                .verifyComplete();
    }

    @Test
    void testNavigerTilBestillingNotFound() {
        Long bestillingId = 1L;

        when(bestillingService.fetchBestillingById(any())).thenReturn(null);

        Mono<RsWhereAmI> result = navigasjonService.navigerTilBestilling(bestillingId);

        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }
}