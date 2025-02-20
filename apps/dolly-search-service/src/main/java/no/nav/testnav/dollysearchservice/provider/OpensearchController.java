package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.service.OpenSearchService;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

import static no.nav.testnav.dollysearchservice.dto.DollyBackendSelector.DEV;
import static no.nav.testnav.dollysearchservice.dto.DollyBackendSelector.REGULAR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RestController
@RequestMapping("/api/v1/opensearch")
@RequiredArgsConstructor
public class OpensearchController {

    private final OpenSearchService openSearchService;

    @PostMapping
    @Operation(description = "Henter personer som matcher søk av persondetaljer i request")
    public Mono<SearchResponse> getIdenterMed(@RequestHeader Map<String, String> headers,
                                              @Schema(description = "Ikke filtrer søkeresultat for " +
                                                      "eksisterende personer (default er filtrering)")
                                              @RequestParam(required = false) Boolean ikkeFiltrer,
                                              @RequestBody SearchRequest request) {

        var kallendeApp = isNotBlank(headers.get("Origin")) ? headers.get("Origin") : headers.get("origin");
        log.info("Kallende applikasjon: {}", kallendeApp);

        var selector = kallendeApp.contains("ekstern") ? REGULAR : DEV;

        return openSearchService.search(request, selector, ikkeFiltrer);
    }
}
