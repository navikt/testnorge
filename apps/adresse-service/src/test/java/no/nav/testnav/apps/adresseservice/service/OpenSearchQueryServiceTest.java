package no.nav.testnav.apps.adresseservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.VegadresseDTO;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenSearchQueryServiceTest {

    @Mock
    private OpenSearchClient openSearchClient;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private SearchResponse<JsonNode> sokeresultat;

    @Mock
    private HitsMetadata<JsonNode> hitsMetadata;

    @Mock
    private Hit<JsonNode> hit;

    @InjectMocks
    private OpenSearchQueryService openSearchQueryService;

    @Test
    void vegadresseQuery_IngenTreff()  throws Exception {

        val vegadresseRequest = VegadresseRequest.builder().build();

        when(openSearchClient.search(any(SearchRequest.class), eq(JsonNode.class)))
                .thenReturn(sokeresultat);

        val target = openSearchQueryService.execQuery(vegadresseRequest, 1L);

        assertThat(target, is(empty()));
    }

    @Test
    void vegadresseQuery_OK() throws Exception {

        val vegadresseRequest = VegadresseRequest.builder().build();

        when(openSearchClient.search(any(SearchRequest.class), eq(JsonNode.class)))
                .thenReturn(sokeresultat);

        when(sokeresultat.hits())
                .thenReturn(hitsMetadata);
        when(hitsMetadata.hits())
                .thenReturn(java.util.Collections.singletonList(hit));
        when(hit.source())
                .thenReturn(Mockito.mock(JsonNode.class));

        when(objectMapper.treeToValue(any(JsonNode.class), eq(VegadresseDTO.class)))
                .thenReturn(new VegadresseDTO());
        when(mapperFacade.map(any(VegadresseDTO.class), eq(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.class)))
                .thenReturn(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                        .adressenavn("Testveg 1")
                        .build());

        val target = openSearchQueryService.execQuery(vegadresseRequest, 1L);

        assertThat(target.getFirst().getAdressenavn(), is(equalTo("Testveg 1")));
    }

    @Test
    void vegadresseQuery_kallFeilet() throws Exception {

        val vegadresseRequest = VegadresseRequest.builder().build();

        when(openSearchClient.search(any(SearchRequest.class), eq(JsonNode.class)))
                .thenThrow(new IOException("Feil ved kall til OpenSearch"));

        Assertions.assertThrows(
                InternalError.class,
                () -> openSearchQueryService.execQuery(vegadresseRequest, 1L),
                    "Feil ved adressesøk i OpenSearch"
                );
    }

    @Test
    void matrikkeladresseQuery_objectMappingFeilet() throws Exception {

        val matrikkadresseRequest = MatrikkeladresseRequest.builder().build();

        when(openSearchClient.search(any(SearchRequest.class), eq(JsonNode.class)))
                .thenReturn(sokeresultat);

        when(sokeresultat.hits())
                .thenReturn(hitsMetadata);
        when(hitsMetadata.hits())
                .thenReturn(java.util.Collections.singletonList(hit));
        when(hit.source())
                .thenReturn(Mockito.mock(JsonNode.class));

        when(objectMapper.treeToValue(any(JsonNode.class), eq(MatrikkeladresseDTO.class)))
                .thenThrow(new IllegalArgumentException("Feil ved mapping av matrikkeladresse"));

        val target = openSearchQueryService.execQuery(matrikkadresseRequest, 1L);

        assertThat(target, is(empty()));
    }

    @Test
    void matrikkeladresseQuery_orikaMappingFeilet() throws Exception {

        val matrikkadresseRequest = MatrikkeladresseRequest.builder().build();

        when(openSearchClient.search(any(SearchRequest.class), eq(JsonNode.class)))
                .thenReturn(sokeresultat);

        when(sokeresultat.hits())
                .thenReturn(hitsMetadata);
        when(hitsMetadata.hits())
                .thenReturn(java.util.Collections.singletonList(hit));
        when(hit.source())
                .thenReturn(Mockito.mock(JsonNode.class));

        when(objectMapper.treeToValue(any(JsonNode.class), eq(MatrikkeladresseDTO.class)))
                .thenReturn(new MatrikkeladresseDTO());

        when(mapperFacade.map(any(MatrikkeladresseDTO.class), eq(no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO.class)))
                .thenThrow(new NullPointerException());

        val target = openSearchQueryService.execQuery(matrikkadresseRequest, 1L);

        assertThat(target, is(empty()));
    }

    @Test
    void matrikkeladresseQuery_OK() throws Exception {

        val matrikkadresseRequest = MatrikkeladresseRequest.builder().build();

        when(openSearchClient.search(any(SearchRequest.class), eq(JsonNode.class)))
                .thenReturn(sokeresultat);

        when(sokeresultat.hits())
                .thenReturn(hitsMetadata);
        when(hitsMetadata.hits())
                .thenReturn(java.util.Collections.singletonList(hit));
        when(hit.source())
                .thenReturn(Mockito.mock(JsonNode.class));

        when(objectMapper.treeToValue(any(JsonNode.class), eq(MatrikkeladresseDTO.class)))
                .thenReturn(new MatrikkeladresseDTO());

        when(mapperFacade.map(any(MatrikkeladresseDTO.class), eq(no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO.class)))
                .thenReturn(no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO.builder()
                        .tilleggsnavn("Testveg 1")
                        .build());

        val target = openSearchQueryService.execQuery(matrikkadresseRequest, 1L);

        assertThat(target.getFirst().getTilleggsnavn(), is(equalTo("Testveg 1")));
    }
}