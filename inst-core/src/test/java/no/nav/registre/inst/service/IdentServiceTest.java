package no.nav.registre.inst.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private Inst2Consumer inst2Consumer;

    @Mock
    private Inst2FasitService inst2FasitService;

    @InjectMocks
    private IdentService identService;

    private String fregTokenProviderUrl = "dummyUrl";
    private String id = "test";
    private Long oppholdId1 = 1L;
    private Long oppholdId2 = 2L;
    private List<Institusjonsopphold> meldinger;
    private String miljoe = "t1";

    @Before
    public void setUp() {
        meldinger = new ArrayList<>();
        meldinger.add(Institusjonsopphold.builder()
                .oppholdId(oppholdId1)
                .tssEksternId("123")
                .startdato(LocalDate.of(2019, 1, 1))
                .faktiskSluttdato(LocalDate.of(2019, 2, 1))
                .build());
        meldinger.add(Institusjonsopphold.builder()
                .oppholdId(oppholdId2)
                .tssEksternId("456")
                .startdato(LocalDate.of(2019, 1, 1))
                .faktiskSluttdato(LocalDate.of(2019, 2, 1))
                .build());
    }

    @Test
    public void shouldSletteInstitusjonsforhold() {
        String fnr1 = "01010101010";
        String fnr2 = "02020202020";
        List<String> identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        when(inst2FasitService.getFregTokenProviderInEnvironment(miljoe)).thenReturn(fregTokenProviderUrl);
        when(inst2Consumer.hentInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(fnr1))).thenReturn(Collections.singletonList(meldinger.get(0)));
        when(inst2Consumer.hentInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(fnr2))).thenReturn(Collections.singletonList(meldinger.get(1)));
        when(inst2Consumer.slettInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(oppholdId1))).thenReturn(ResponseEntity.noContent().build());
        when(inst2Consumer.slettInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(oppholdId2))).thenReturn(ResponseEntity.noContent().build());

        List<OppholdResponse> response = identService.slettInstitusjonsoppholdTilIdenter(id, id, miljoe, identer);

        verify(inst2Consumer).hentTokenTilInst2(fregTokenProviderUrl);
        verify(inst2Consumer).hentInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(fnr1));
        verify(inst2Consumer).hentInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(fnr2));
        verify(inst2Consumer).slettInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(oppholdId1));
        verify(inst2Consumer).slettInstitusjonsoppholdFraInst2(anyMap(), eq(id), eq(id), eq(miljoe), eq(oppholdId2));

        assertThat(response.get(0).getInstitusjonsopphold().getOppholdId(), equalTo(oppholdId1));
        assertThat(response.get(1).getInstitusjonsopphold().getOppholdId(), equalTo(oppholdId2));
    }
}