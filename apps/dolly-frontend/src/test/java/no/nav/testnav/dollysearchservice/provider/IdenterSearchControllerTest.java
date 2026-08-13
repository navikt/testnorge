package no.nav.testnav.dollysearchservice.provider;

import no.nav.testnav.dollysearchservice.service.IdenterSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdenterSearchControllerTest {

    @Mock
    private IdenterSearchService identerSearchService;

    @InjectMocks
    private IdenterSearchController identerSearchController;

    @Test
    void shouldForwardPaginationParameters() {

        when(identerSearchService.getIdenter("ola", 2, 20, 123))
                .thenReturn(List.of());

        assertThat(identerSearchController.getIdenter("ola", 2, 20, 123)
                .collectList()
                .block())
                .isEmpty();

        verify(identerSearchService).getIdenter("ola", 2, 20, 123);
    }

    @Test
    void shouldReturnEmptyResultForBlankFragment() {

        assertThat(identerSearchController.getIdenter(" ", 0, 10, null)
                .collectList()
                .block())
                .isEmpty();

        verifyNoInteractions(identerSearchService);
    }
}
