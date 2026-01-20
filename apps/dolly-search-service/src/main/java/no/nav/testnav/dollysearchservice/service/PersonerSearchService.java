package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.mapper.MappingContextUtils;
import no.nav.testnav.dollysearchservice.utils.OpenSearchQueryBuilder;
import no.nav.testnav.libs.dto.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchIdenterQueryUtils.addIdenterQuery;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonerSearchService {

    private static final String NO_IDENT = "9999999999)";

    private final BestillingQueryService bestillingQueryService;
    private final MapperFacade mapperFacade;
    private final OpenSearchQueryService openSearchQueryService;

    public Mono<SearchResponse> search(SearchRequest searchRequest, List<ElasticTyper> registreRequest) {

        log.info("Mottok søkeforespørsel: searchRequest={}, registreRequest={}",
                searchRequest, registreRequest);

        if (isNull(searchRequest)) {
            log.warn("SearchRequest er null, returnerer tom respons");
            return Mono.just(new SearchResponse());
        }

        var safeRegistreRequest = isNull(registreRequest) ? Collections.<ElasticTyper>emptyList() : registreRequest;

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("registreRequest", safeRegistreRequest);

        var request = mapperFacade.map(searchRequest,
                no.nav.testnav.dollysearchservice.dto.SearchRequest.class, context);

        log.debug("Mappet request: {}", request);

        Set<String> identer;
        if (nonNull(request.getPersonRequest()) && isNotBlank(request.getPersonRequest().getIdent())) {

            log.debug("Utfører registersøk uten cache for ident: {}", request.getPersonRequest().getIdent());
            identer = bestillingQueryService.execRegisterNoCacheQuery(request).stream()
                    .filter(ident -> ident.equals(request.getPersonRequest().getIdent()))
                    .collect(Collectors.toSet());

        } else {

            log.debug("Utfører registersøk med cache");
            identer = bestillingQueryService.execRegisterCacheQuery(request);
        }

        log.debug("Fant {} identer fra bestillingsquery", identer.size());

        request.setIdenter(identer.isEmpty() ? Set.of(NO_IDENT) : identer);

        var query = OpenSearchQueryBuilder.buildSearchQuery(request);
        addIdenterQuery(query, request.getIdenter());

        return openSearchQueryService.execQuery(request, query)
                .map(response -> mapperFacade.map(response, SearchResponse.class))
                .doOnError(error -> log.error("Feil ved søk i OpenSearch: {}", error.getMessage(), error));
    }

    public List<Kategori> getTyper() {

        return Stream.of(ElasticTyper.values())
                .map(entry -> Kategori.builder()
                        .type(entry.name())
                        .beskrivelse(entry.getBeskrivelse())
                        .build())
                .sorted(Comparator.comparing(Kategori::getBeskrivelse))
                .toList();
    }
}