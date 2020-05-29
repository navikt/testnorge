package no.nav.registre.tp.provider.rs;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.registre.tp.provider.rs.request.OrkestreringRequest;
import no.nav.registre.tp.service.TpService;

@RunWith(SpringJUnit4ClassRunner.class)
public class OrkestreringControllerTest {

    @Mock
    private TpService tpService;

    @InjectMocks
    private OrkestreringController orkestreringController;

    @Test
    public void initializeDatabase() {
        when(tpService.initializeTpDbForEnvironment(anyLong())).thenReturn(1);
        ResponseEntity entity = orkestreringController.initializeDatabase(new OrkestreringRequest(1L, "q2"));
        assertEquals(1, entity.getBody());
    }

    @Test
    public void addPeople() {
        List<String> fnrs = new ArrayList<>();
        fnrs.add("123");
        fnrs.add("132");
        fnrs.add("321");

        List<String> feiletPersoner = new ArrayList<>(1);
        feiletPersoner.add("123");
        when(tpService.createPeople(any())).thenReturn(feiletPersoner);
        var entity = orkestreringController.addPeople("q", fnrs);
        assertEquals(2, Objects.requireNonNull(entity.getBody()).size());

    }
}