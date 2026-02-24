package no.nav.testnav.apps.adresseservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import no.nav.testnav.apps.adresseservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchQueryService {

    private final OpenSearchClient openSearchClient;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;

    @Value("${opensearch.index.adresser}")
    private String adresseIndex;

    public List<VegadresseDTO> execQuery(VegadresseRequest request, Long antall) {

        var queryBuilder = OpenSearchQueryBuilder.buildSearchQuery(request);

        return execQuery(queryBuilder, new no.nav.testnav.apps.adresseservice.dto.VegadresseDTO(), new VegadresseDTO(), antall);
    }

    public List<MatrikkeladresseDTO> execQuery(MatrikkeladresseRequest request, Long antall) {

        var queryBuilder = OpenSearchQueryBuilder.buildSearchQuery(request);

        return execQuery(queryBuilder, new no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO(), new MatrikkeladresseDTO(), antall);
    }

    private <S,T> List<T> execQuery(BoolQuery.Builder queryBuilder, S kilde, T destinasjon, Long antall) {

        try {
            val adresseSoekResponse = openSearchClient.search(new SearchRequest.Builder()
                    .index(adresseIndex)
                    .query(new Query.Builder()
                            .bool(queryBuilder.build())
                            .build())
                    .size(antall.intValue())
                    .timeout("3s")
                    .build(), JsonNode.class);

            return formatResponse(adresseSoekResponse, kilde, destinasjon);

        } catch (IOException e) {

            log.error("Feil ved adressesøk i OpenSearch", e);
            throw new InternalError("Feil ved adressesøk i OpenSearch", e);
        }
    }


    private <T, S> List<T> formatResponse(SearchResponse<JsonNode> response, S kilde, T destinasjon) {

        if (nonNull(response.hits()) && nonNull(response.hits().hits())) {

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(source -> {
                        try {
                            val data = objectMapper.treeToValue(source, kilde.getClass());
                            return mapperFacade.map(data, destinasjon.getClass());
                        } catch (Exception e) {
                            log.error("Feil ved mapping av {} fra OpenSearch", destinasjon.getClass().getSimpleName(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .map(dto -> (T) dto)
                    .toList();
        }
        return Collections.emptyList();
    }
}
