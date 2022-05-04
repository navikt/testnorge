package no.nav.registre.testnorge.personsearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personsearchservice.adapter.IdentSearchAdapter;
import no.nav.testnav.libs.dto.personsearchservice.v1.IdentdataDTO;
import no.nav.registre.testnorge.personsearchservice.domain.IdentSearch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final IdentSearchAdapter identSearchAdapter;

    public List<JsonNode> getIdenter(String query) {

        return identSearchAdapter.search(getSearchCriteria(query));
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
                .excludeTags(List.of("ARENASYNT"))
                .tag("DOLLY")
                .ident(ident)
                .navn(navn)
                .build();
    }
}
