package no.nav.testnav.identpool.service;

import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentpoolServiceTest {

    private static final String fnr1 = "01010101010";
    private static final String fnr2 = "02020202020";

    @Mock
    private IdentRepository identRepository;

    @Mock
    private TpsMessagingConsumer tpsMessagingConsumer;

    @InjectMocks
    private IdentpoolService identpoolService;

    @Test
    void frigjoerRekvirertMenLedigIdentTest() {

        var ident1 = Ident.builder()
                .personidentifikator(fnr1)
                .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                .syntetisk(isSyntetisk(fnr1))
                .build();

        var ident2 = Ident.builder()
                .personidentifikator(fnr1)
                .rekvireringsstatus(LEDIG)
                .syntetisk(isSyntetisk(fnr1))
                .build();

        when(identRepository.findByPersonidentifikatorIn(anyList())).thenReturn(Flux.just(ident1));
        when(identRepository.save(ident2)).thenReturn(Mono.just(ident2));

        identpoolService.frigjoerIdenter(List.of(fnr1))
                .as(StepVerifier::create)
                .expectNext(List.of(fnr1))
                .verifyComplete();
    }

    @Test
    void finnesIProd_happyTest() {

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet()))
                .thenReturn(Flux.just(TpsStatusDTO.builder()
                        .ident(fnr2)
                        .inUse(false)
                        .build(), TpsStatusDTO.builder()
                        .ident(fnr2)
                        .inUse(true)
                        .build()));

        identpoolService.finnesIProd(Set.of(fnr1, fnr2))
                .as(StepVerifier::create)
                .assertNext(tpsStatusDTO -> {
                    assertEquals(fnr2, tpsStatusDTO.getIdent());
                    assertTrue(tpsStatusDTO.isInUse());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void lseInnhold_happyTest() {

        Ident ident = Ident.builder()
                .personidentifikator(fnr1)
                .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                .syntetisk(isSyntetisk(fnr1))
                .build();

        when(identRepository.findByPersonidentifikator(fnr1)).thenReturn(Mono.just(ident));

        identpoolService.lesInnhold(fnr1)
                .as(StepVerifier::create)
                .expectNext(ident)
                .verifyComplete();
    }

    @Test
    void leseInnhold_ikkeFunnet() {

        when(identRepository.findByPersonidentifikator(fnr1)).thenReturn(Mono.empty());

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> StepVerifier.create(
                                identpoolService.lesInnhold(fnr1))
                        .expectComplete()
                        .verify())
                .withMessage("expectation \"expectComplete\" failed (expected: onComplete(); actual: onError(org.springframework.web.server.ResponseStatusException: 404 NOT_FOUND \"Fant ikke ident 01010101010\"))");
    }

    @Test
    void hentLedigeFNRFoedtMellomSyntetiskTest() {

        var ident = Ident.builder()
                .rekvireringsstatus(LEDIG)
                .foedselsdato(LocalDate.of(1992, 3, 1))
                .identtype(Identtype.FNR)
                .kjoenn(Kjoenn.MANN)
                .personidentifikator("123")
                .build();

        when(identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(LocalDate.of(1991, 1, 1),
                LocalDate.of(2000, 1, 1),
                Identtype.FNR, LEDIG,
                true)).
                thenReturn(Flux.just(ident));

        identpoolService.hentLedigeFNRFoedtMellom(LocalDate.of(1991, 1, 1),
                        LocalDate.of(2000, 1, 1), true)
                .as(StepVerifier::create)
                .assertNext(tpsStatusDTO -> {
                    assertThat(tpsStatusDTO, containsInAnyOrder("123"));
                })
                .verifyComplete();
    }
}