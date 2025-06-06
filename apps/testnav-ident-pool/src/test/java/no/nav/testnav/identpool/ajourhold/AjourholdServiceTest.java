package no.nav.testnav.identpool.ajourhold;

import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.service.IdentGeneratorService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static no.nav.testnav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjourholdServiceTest {

    private static final String FNR1 = "01010101010";
    private static final String FNR2 = "02020202020";

    @Mock
    private TpsMessagingConsumer tpsMessagingConsumer;

    @Mock
    private IdentRepository identRepository;

    private AjourholdService ajourholdService;

    @BeforeEach
    void init() {

        ajourholdService = Mockito.spy(new AjourholdService(new IdentGeneratorService(), identRepository, tpsMessagingConsumer));
    }

    @Test
    void genererIdenterForAarHvorIngenErLedige() {

        var year = LocalDate.now().getYear() - 50; // 50 years ago
        when(identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31),
                Identtype.FNR, Rekvireringsstatus.LEDIG, false)).thenReturn(Flux.empty());
        when(identRepository.existsByPersonidentifikator(anyString())).thenReturn(Mono.just(true));
        ajourholdService.checkAndGenerateForYear(year, Identtype.FNR, false)
                .as(StepVerifier::create)
                .expectNextMatches(result ->
                        result.startsWith("Ajourhold: år " + year + " FNR vanlige identer har fått allokert antall nye: 0"))
                .verifyComplete();
    }

    @Test
    void genererIdenterForAarHvorAlleErLedige() {

        var year = LocalDate.now().getYear() - 30; // 30 years ago
        when(identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31),
                Identtype.FNR, Rekvireringsstatus.LEDIG, true)).thenReturn(Flux.empty());
        when(identRepository.existsByPersonidentifikator(anyString())).thenReturn(Mono.just(false));
        when(identRepository.save(any(Ident.class))).thenReturn(Mono.just(new Ident()));

        ajourholdService.checkAndGenerateForYear(year, Identtype.FNR, true)
                .as(StepVerifier::create)
                .expectNextMatches(result ->
                        result.startsWith("Ajourhold: år " + year + " FNR NAV-syntetiske identer har fått allokert antall nye: 1460"))
                .verifyComplete();
    }

    @Test
    void getIdentsAndCheckProd_happyTest() {

        when(identRepository.countAllIkkeSyntetisk(eq(LEDIG), any(LocalDate.class)))
                .thenReturn(Mono.just(100));
        when(identRepository.findAllIkkeSyntetisk(eq(LEDIG), any(LocalDate.class), any(PageRequest.class)))
                .thenReturn(Flux.just(prepIdent(FNR1, LEDIG, false)))
                .thenReturn(Flux.just(prepIdent(FNR2, LEDIG, false)));
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet()))
                .thenReturn(Flux.just(TpsStatusDTO.builder()
                                .ident(FNR1)
                                .inUse(true)
                        .build(),
                        TpsStatusDTO.builder()
                                .ident(FNR2)
                                .inUse(true)
                        .build()));
        when(identRepository.findByPersonidentifikator(anyString()))
                .thenReturn(Mono.just(prepIdent(FNR1, LEDIG, false)))
                .thenReturn(Mono.just(prepIdent(FNR2, LEDIG, false)));
        when(identRepository.save(any(Ident.class)))
                .thenReturn(Mono.just(prepIdent(FNR1, I_BRUK, false)))
                .thenReturn(Mono.just(prepIdent(FNR2, I_BRUK, false)));

        ajourholdService.getIdentsAndCheckProd()
                .as(StepVerifier::create)
                .assertNext(count -> assertThat(count, Matchers.is(2L)))
                .verifyComplete();
    }

    private static Ident prepIdent(String ident, Rekvireringsstatus status, boolean syntetisk) {

        return Ident.builder()
                .personidentifikator(ident)
                .rekvireringsstatus(status)
                .syntetisk(syntetisk)
                .build();
    }
}
