package no.nav.testnav.dollysearchservice.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdenterSearchServiceTest {

    @Mock
    private BestillingQueryService bestillingQueryService;
    @Mock
    private OpenSearchQueryService personQueryService;
    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private IdenterSearchService identerSearchService;

    @Test
    void shouldForwardPageSizeAndSeedToOpenSearch() {

        when(bestillingQueryService.execTestnorgeIdenterQuery())
                .thenReturn(Set.of());
        when(personQueryService.execQuery(any(), any()))
                .thenReturn(SearchInternalResponse.builder()
                        .personer(List.of())
                        .build());

        identerSearchService.getIdenter("ola", 2, 20, 123);
        identerSearchService.getIdenter("ola", 3, 20, 456);

        var requestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
        var queryCaptor = ArgumentCaptor.forClass(FunctionScoreQuery.Builder.class);
        verify(personQueryService, times(2)).execQuery(requestCaptor.capture(), queryCaptor.capture());

        assertThat(requestCaptor.getAllValues())
                .extracting(SearchRequest::getSide)
                .containsExactly(2, 3);
        assertThat(requestCaptor.getAllValues())
                .extracting(SearchRequest::getAntall)
                .containsOnly(20);
        assertThat(requestCaptor.getAllValues())
                .extracting(SearchRequest::getSeed)
                .containsExactly(123, 456);
        assertThat(queryCaptor.getAllValues())
                .extracting(query -> query.build()
                        .functions()
                        .getFirst()
                        .randomScore()
                        .seed()
                        .to(Integer.class))
                .containsExactly(123, 456);
        verify(bestillingQueryService).execTestnorgeIdenterQuery();
    }
}
