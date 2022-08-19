package no.nav.testnav.apps.tpservice.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import no.nav.testnav.apps.tpservice.provider.request.OrkestreringRequest;
import no.nav.testnav.apps.tpservice.service.TpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class OrkestreringControllerTest {

    @Mock
    private TpService tpService;

    @InjectMocks
    private OrkestreringController orkestreringController;

    @Test
    public void initializeDatabase() {
        when(tpService.initializeTpDbForEnvironment(anyLong())).thenReturn(1);
        var entity = orkestreringController.initializeDatabase(new OrkestreringRequest(1L, "q2"));
        assertThat(entity.getBody()).isEqualTo(1);
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

        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).hasSize(2);
    }
}