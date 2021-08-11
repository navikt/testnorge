package no.nav.registre.inst.service;

import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.security.TokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private Inst2Consumer inst2Consumer;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private IdentService identService;

    private String token = "Bearer 123";


    @Test
    public void shouldSletteInstitusjonsforhold() {
        var fnr1 = "01010101010";
        var fnr2 = "02020202020";
        var identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        var miljoe = "t1";
        var id = "test";

        when(tokenService.getIdTokenT()).thenReturn(token);
        when(inst2Consumer.slettInstitusjonsoppholdMedIdent(anyString(), eq(id), eq(id), eq(miljoe), eq(fnr1))).thenReturn(ResponseEntity.noContent().build());
        when(inst2Consumer.slettInstitusjonsoppholdMedIdent(anyString(), eq(id), eq(id), eq(miljoe), eq(fnr2))).thenReturn(ResponseEntity.noContent().build());

        identService.slettInstitusjonsoppholdTilIdenter(id, id, miljoe, identer);

        verify(tokenService).getIdTokenT();
        verify(inst2Consumer).slettInstitusjonsoppholdMedIdent(token, id, id, miljoe, fnr1);
        verify(inst2Consumer).slettInstitusjonsoppholdMedIdent(token, id, id, miljoe, fnr2);
    }
}