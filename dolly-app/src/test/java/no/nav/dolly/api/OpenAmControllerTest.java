package no.nav.dolly.api;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.domain.resultset.RsOpenAmRequest;
import no.nav.dolly.service.OpenAmService;

@RunWith(MockitoJUnitRunner.class)
public class OpenAmControllerTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String MILJOE1 = "t0";
    private static final String MILJOE2 = "t1";

    @Mock
    private OpenAmService openAmService;

    @InjectMocks
    private OpenAmController openAmController;

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
}