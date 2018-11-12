package no.nav.dolly.api;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsOpenAmRequest;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.GruppeRepository;
import no.nav.dolly.service.OpenAmService;

@RunWith(MockitoJUnitRunner.class)
public class OpenAmControllerTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String MILJOE1 = "t0";
    private static final String MILJOE2 = "t1";
    private static final long GRUPPEID = 1L;
    private static final Boolean STATUS = true;

    @Mock
    private OpenAmService openAmService;

    @Mock GruppeRepository gruppeRepository;

    @InjectMocks
    private OpenAmController openAmController;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Testgruppe testgruppe;

    private ArgumentCaptor<List<String>> captor;

    @Before
    public void setup() {
        captor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    public void opprettIdenterOk() {
        openAmController.opprettIdenter(RsOpenAmRequest.builder()
                .identer(asList(IDENT1, IDENT2))
                .miljoer(asList(MILJOE1, MILJOE2))
                .build());

        verify(openAmService).opprettIdenter(captor.capture(), eq(MILJOE1));
        verify(openAmService).opprettIdenter(captor.capture(), eq(MILJOE2));
        assertThat(captor.getValue(), containsInAnyOrder(IDENT1, IDENT2));
    }

    @Test
    public void endreOpenAmSentStatusOk() {
        when(gruppeRepository.findById(GRUPPEID)).thenReturn(Optional.of(testgruppe));

        openAmController.oppdaterOpenAmSentStatus(GRUPPEID, STATUS);
        verify(gruppeRepository).findById(GRUPPEID);
        verify(gruppeRepository).save(any(Testgruppe.class));
    }

    @Test
    public void endreOpenAmSentStatusGruppeNotFound() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(format("GruppeId %d ble ikke funnet.", GRUPPEID));

        openAmController.oppdaterOpenAmSentStatus(GRUPPEID, STATUS);
        verify(gruppeRepository).findById(GRUPPEID);
        verify(gruppeRepository).save(any(Testgruppe.class));
    }
}