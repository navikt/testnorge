package no.nav.testnav.dollysearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.dollysearchservice.dto.IdentSearch;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.utils.QueryBuilder;
import no.nav.testnav.libs.data.dollysearchservice.v1.IdentdataDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final BestillingQueryService bestillingQueryService;
    private final PersonQueryService personQueryService;
    private final MapperFacade mapperFacade;

    @SneakyThrows
    public Flux<IdentdataDTO> getIdenter(String fragment) {

        var query = QueryBuilder.buildIdentSearchQuery(getSearchCriteria(fragment));
        return personQueryService.execQuery(new SearchRequest(), query)
                .map(this::formatResponse)
                .map(this::filterRegister)
                .flatMapMany(Flux::fromIterable);
    }

    private IdentSearch getSearchCriteria(String query) {

        var ident = Stream.of(query.split(" "))
                .filter(StringUtils::isNumeric)
                .findFirst()
                .orElse(null);

        var navn = Stream.of(query.split(" "))
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .toList();

        return IdentSearch.builder()
                .page(1)
                .pageSize(10)
                .tags(List.of("DOLLY", "TESTNORGE"))
                .ident(ident)
                .navn(navn)
                .build();
    }

    private List<IdentdataDTO> formatResponse(SearchInternalResponse response) {

        return response.getPersoner().stream()
                .map(person -> mapperFacade.map(person, IdentdataDTO.class))
                .toList();
    }

    private List<IdentdataDTO> filterRegister(List<IdentdataDTO> identdataDTO) {

        var identer = bestillingQueryService.execRegisterQuery(SearchRequest.builder()
                .identer(identdataDTO.stream().map(IdentdataDTO::getIdent).collect(Collectors.toSet()))
                .build());

        return identdataDTO.stream()
                .filter(identdata -> identer.contains(identdata.getIdent()))
                .toList();
    }
}
