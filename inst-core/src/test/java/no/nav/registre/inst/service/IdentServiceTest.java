package no.nav.registre.inst.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.inst.Institusjonsforholdsmelding;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private Inst2Consumer inst2Consumer;

    @InjectMocks
    private IdentService identService;

    private String oppholdId1 = "1";
    private String oppholdId2 = "2";
    private List<Institusjonsforholdsmelding> meldinger;

    @Before
    public void setUp() {
        meldinger = new ArrayList<>();
        meldinger.add(Institusjonsforholdsmelding.builder()
                .oppholdId(oppholdId1)
                .tssEksternId("123")
                .startdato("2019-01-01")
                .faktiskSluttdato("2019-02-01")
                .build());
        meldinger.add(Institusjonsforholdsmelding.builder()
                .oppholdId(oppholdId2)
                .tssEksternId("456")
                .startdato("2019-01-01")
                .faktiskSluttdato("2019-02-01")
                .build());
    }

    @Test
    public void shouldSletteInstitusjonsforhold() {
        String fnr1 = "01010101010";
        String fnr2 = "02020202020";
        List<String> identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        when(inst2Consumer.hentInstitusjonsoppholdFraInst2(anyMap(), eq(fnr1))).thenReturn(Collections.singletonList(meldinger.get(0)));
        when(inst2Consumer.hentInstitusjonsoppholdFraInst2(anyMap(), eq(fnr2))).thenReturn(Collections.singletonList(meldinger.get(1)));
        when(inst2Consumer.slettInstitusjonsoppholdFraInst2(anyMap(), eq(oppholdId1))).thenReturn(ResponseEntity.noContent().build());
        when(inst2Consumer.slettInstitusjonsoppholdFraInst2(anyMap(), eq(oppholdId2))).thenReturn(ResponseEntity.noContent().build());

        List<String> slettedeOppholdIder = identService.slettInstitusjonsforholdTilIdenter(identer);

        verify(inst2Consumer).hentTokenTilInst2();
        verify(inst2Consumer).hentInstitusjonsoppholdFraInst2(anyMap(), eq(fnr1));
        verify(inst2Consumer).hentInstitusjonsoppholdFraInst2(anyMap(), eq(fnr2));
        verify(inst2Consumer).slettInstitusjonsoppholdFraInst2(anyMap(), eq(oppholdId1));
        verify(inst2Consumer).slettInstitusjonsoppholdFraInst2(anyMap(), eq(oppholdId2));

        assertThat(slettedeOppholdIder, IsIterableContainingInOrder.contains(oppholdId1, oppholdId2));
    }
}