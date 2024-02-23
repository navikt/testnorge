package no.nav.testnav.apps.oppsummeringsdokumentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oppsummeringsdokumentservice.consumer.AaregSyntConsumer;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.QueryRequest;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.QueryResponse;
import no.nav.testnav.apps.oppsummeringsdokumentservice.repository.OppsummeringsdokumentRepository;
import no.nav.testnav.apps.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.oppsummeringsdokumentservice.service.SearchQueryUtility.prepareQuery;

@Slf4j
@Service
@RequiredArgsConstructor
public class OppsummeringsdokumentService {

    private static final int PAGE_SIZE = 50;
    private final ObjectMapper objectMapper;
    private final OppsummeringsdokumentRepository repository;
    private final AaregSyntConsumer aaregSyntConsumer;
    private final RestHighLevelClient restHighLevelClient;

    @Value("${open.search.index}")
    private String DOCUMENT_INDEX;

    public void deleteAllBy(String miljo, Populasjon populasjon) {
        repository.deleteAllByMiljoAndPopulasjon(miljo, populasjon);
    }

    public void deleteAllBy(String miljo) {
        repository.deleteAllByMiljo(miljo);
    }

    @SneakyThrows
    public String save(Oppsummeringsdokument oppsummeringsdokument, String miljo, String origin) {

        log.info("Oppretter oppsummeringsdokument for opplysningspliktig {} i {}...",
                oppsummeringsdokument.getOpplysningspliktigOrganisajonsnummer(), miljo);
        aaregSyntConsumer.saveOpplysningspliktig(oppsummeringsdokument, miljo);

        try {
            var id = repository.save(oppsummeringsdokument.toModel(miljo, origin)).getId();
            log.info("Oppsummeringsdokument (id: {}) opprett for opplysningsplikitg {} i {}.", id, oppsummeringsdokument.getOpplysningspliktigOrganisajonsnummer(), miljo);
            return id;

        } catch (UncategorizedElasticsearchException ex) {
            log.error("Feil ved innsending av \n{}", objectMapper.writeValueAsString(oppsummeringsdokument.toDTO()), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ElasticSearch feil ved innlegging");
        }
    }

    public Optional<Oppsummeringsdokument> getCurrentDocumentsBy(String id) {

        var resultat = getAllCurrentDocumentsBy(QueryRequest.builder()
                .id(id)
                .build())
                .getDokumenter();

        return resultat.isEmpty() ?
                Optional.empty() :
                Optional.of(resultat.getFirst());
    }

    public Optional<Oppsummeringsdokument> getCurrentDocumentBy(LocalDate kalendermaaned, String orgnummer, String miljo) {

        var resultat = getAllCurrentDocumentsBy(QueryRequest.builder()
                .fom(kalendermaaned)
                .tom(kalendermaaned)
                .orgnummer(orgnummer)
                .miljo(miljo)
                .build())
                .getDokumenter();

        return resultat.isEmpty() ?
                Optional.empty() :
                Optional.of(resultat.getFirst());
    }

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo) {

        return getAllCurrentDocumentsBy(QueryRequest.builder()
                .miljo(miljo)
                .build())
                .getDokumenter();
    }

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, Integer page) {

        return getAllCurrentDocumentsBy(QueryRequest.builder()
                .miljo(miljo)
                .page(page)
                .build())
                .getDokumenter();
    }

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, String ident) {

        return getAllCurrentDocumentsBy(QueryRequest.builder()
                .miljo(miljo)
                .ident(ident)
                .build())
                .getDokumenter();
    }

    private QueryResponse getAllCurrentDocumentsBy(QueryRequest request) {

        var query = prepareQuery(request);
        var response = execQuery(query, request.getPage(), request.getPageSize());
        return QueryResponse.builder()
                .response(response)
                .dokumenter(getDocuments(response))
                .build();
    }

    private SearchResponse execQuery(QueryBuilder query, Integer page, Integer pageSize) {

        var searchRequest = new SearchRequest(DOCUMENT_INDEX);
        searchRequest.source(new SearchSourceBuilder().query(query)
                .size(nonNull(pageSize) ? pageSize : PAGE_SIZE)
                .from(nonNull(page) ? page : 0));

        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {

            log.error("OpenSearch feil ved utføring av søk: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenSearch feil ved utføring av søk");
        }
    }

    private List<Oppsummeringsdokument> getDocuments(SearchResponse response) {

        var documents = Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsString)
                .filter(Objects::nonNull)
                .map(source -> {
                    try {
                        return objectMapper.readValue(source, OppsummeringsdokumentModel.class);
                    } catch (JsonProcessingException e) {
                        log.error("Json feilet konvertering: {}", e.getMessage(), e);
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JSON feilet konvertering");
                    }
                })
                .toList();

        return filterOnVersion(documents);
    }

    private List<Oppsummeringsdokument> filterOnVersion(List<OppsummeringsdokumentModel> list) {
        return list
                .stream()
                .collect(Collectors.groupingBy(item -> item.getKalendermaaned().withDayOfMonth(1) + item.getOpplysningspliktigOrganisajonsnummer()))
                .values()
                .stream()
                .map(items -> items.stream().reduce(null, (total, value) -> {
                    if (total == null || total.getVersion() < value.getVersion()) {
                        total = value;
                    }
                    return total;
                })).filter(Objects::nonNull)
                .map(Oppsummeringsdokument::new)
                .toList();
    }
}