package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.service.OpenSearchService;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/opensearch")
@RequiredArgsConstructor
public class OpensearchController {

    private final OpenSearchService openSearchService;

    @PostMapping
    @Operation(description = "Henter personer som matcher søk av persondetaljer i request")
    public Mono<SearchResponse> getIdenterMed(@RequestBody SearchRequest request) {

        return openSearchService.search(request);
    }
}
