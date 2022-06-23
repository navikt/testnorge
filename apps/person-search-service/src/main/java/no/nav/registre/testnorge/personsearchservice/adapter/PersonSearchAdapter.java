package no.nav.registre.testnorge.personsearchservice.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.domain.PdlResponse;
import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonSearchAdapter {

    private final ObjectMapper objectMapper;
    private final RestHighLevelClient highLevelClient;

    private <T> List<T> convert(SearchHit[] hits, Class<T> clazz) {
        return Arrays.stream(hits).map(SearchHit::getSourceAsString).map(json -> {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Feil med konvertering fra json: " + json, e);
            }
        }).toList();
    }

    @SneakyThrows
    public PersonList search(SearchRequest searchRequest) {
        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        List<Response> responses = convert(searchResponse.getHits().getHits(), Response.class);

        return new PersonList(
                searchResponse.getHits().getTotalHits().value,
                responses.stream().map(Person::new).toList()
        );
    }

    @SneakyThrows
    public PdlResponse searchWithJsonStringResponse(SearchRequest searchRequest) {
        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        log.info("Fant {} personer i pdl.", totalHits.value);

        var searchHits = searchResponse.getHits().getHits();

        var listResponse = Arrays.stream(searchHits).map(SearchHit::getSourceAsString).toList();
        var jsonResponse = JSONArray.toJSONString(listResponse);

        return new PdlResponse(
                searchResponse.getHits().getTotalHits().value,
                jsonResponse
        );
    }
}