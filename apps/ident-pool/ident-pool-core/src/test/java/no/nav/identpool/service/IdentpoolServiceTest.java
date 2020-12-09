package no.nav.identpool.service;

import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.repository.postgres.IdentRepository;
import no.nav.identpool.test.mockito.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdentpoolServiceTest {

    @Mock
    private IdentRepository repository;

    @Mock
    private IdentTpsService identTpsService;

    @InjectMocks
    private IdentpoolService identpoolService;

    @Test
    void frigjoerRekvirertMenLedigIdentTest() {
        String fnr1 = "01010101010";
        List<String> identer = new ArrayList<>(Collections.singletonList(fnr1));
        Ident ident = Ident.builder()
                .personidentifikator(fnr1)
                .finnesHosSkatt(false)
                .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                .build();

        TpsStatus tpsStatus = new TpsStatus();
        tpsStatus.setIdent(fnr1);
        tpsStatus.setInUse(false);

        Set<TpsStatus> statusSet = new HashSet<>();
        statusSet.add(tpsStatus);

        when(repository.findTopByPersonidentifikator(fnr1)).thenReturn(ident);
        when(identTpsService.checkIdentsInTps(anyList())).thenReturn(statusSet);

        List<String> frigjorteIdenter = identpoolService.frigjoerLedigeIdenter(identer);

        verify(repository).findTopByPersonidentifikator(fnr1);
        verify(identTpsService).checkIdentsInTps(anyList());
        verify(repository).save(ident);

        assertEquals(fnr1, frigjorteIdenter.get(0));
    }

    @Test
    void markerBruktFlereTest() {
        String rekvirertAv = "test";
        String fnr1 = "01010101010";
        String fnr2 = "02020202020";
        List<String> identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        Ident ident1 = Ident.builder()
                .personidentifikator(fnr1)
                .rekvireringsstatus(LEDIG)
                .build();
        TpsStatus tpsStatus = new TpsStatus();
        tpsStatus.setIdent(fnr2);
        tpsStatus.setInUse(false);

        when(repository.findTopByPersonidentifikator(fnr1)).thenReturn(ident1);
        when(repository.findTopByPersonidentifikator(fnr2)).thenReturn(null);
        when(identTpsService.checkIdentsInTps(anyList(), anyList())).thenReturn(new HashSet<>(Collections.singletonList(tpsStatus)));

        List<String> identerMarkertSomIBruk = identpoolService.markerBruktFlere(rekvirertAv, identer);

        assertThat(identerMarkertSomIBruk, containsInAnyOrder(fnr1, fnr2));

        verify(identTpsService).checkIdentsInTps(anyList(), anyList());

    }

    @Test
    void hentLedigeFNRFoedtMellomTest() {
        List<Ident> identer = new ArrayList<>();
        Ident id = new Ident();
        id.setRekvireringsstatus(LEDIG);
        id.setFoedselsdato(LocalDate.of(1992, 3, 1));
        id.setIdenttype(Identtype.FNR);
        id.setKjoenn(Kjoenn.MANN);
        id.setPersonidentifikator("123");
        identer.add(id);
        when(repository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate.of(1991, 1, 1), LocalDate.of(2000, 1, 1), Identtype.FNR, LEDIG)).
                thenReturn(identer);
        List<String> ids = identpoolService.hentLedigeFNRFoedtMellom(LocalDate.of(1991, 1, 1), LocalDate.of(2000, 1, 1));
        assertFalse(ids.isEmpty());
        assertEquals("123", ids.get(0));
    }
}