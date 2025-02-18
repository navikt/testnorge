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

    @PostMapping("/identer")
    @Operation(description = "Henter identer som matcher søk i request, både registre og persondetaljer")
    public Mono<SearchResponse> getIdenterMed(@RequestBody SearchRequest request) {

        return openSearchService.search(request);
    }
}
