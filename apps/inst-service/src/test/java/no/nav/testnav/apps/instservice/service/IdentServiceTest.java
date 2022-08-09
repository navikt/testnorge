package no.nav.testnav.apps.instservice.service;

import no.nav.testnav.apps.instservice.consumer.InstTestdataConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    @Mock
    private InstTestdataConsumer instTestdataConsumer;

    @InjectMocks
    private IdentService identService;

    @Test
    public void shouldSletteInstitusjonsforhold() {
        var fnr1 = "01010101010";
        var fnr2 = "02020202020";
        var identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        var miljoe = "t1";
        var id = "test";

        when(instTestdataConsumer.slettInstitusjonsoppholdMedIdent(id, id, miljoe, fnr1)).thenReturn(ResponseEntity.noContent().build());
        when(instTestdataConsumer.slettInstitusjonsoppholdMedIdent(id, id, miljoe, fnr2)).thenReturn(ResponseEntity.noContent().build());

        identService.slettInstitusjonsoppholdTilIdenter(id, id, miljoe, identer);

        verify(instTestdataConsumer).slettInstitusjonsoppholdMedIdent(id, id, miljoe, fnr1);
        verify(instTestdataConsumer).slettInstitusjonsoppholdMedIdent(id, id, miljoe, fnr2);
    }
}