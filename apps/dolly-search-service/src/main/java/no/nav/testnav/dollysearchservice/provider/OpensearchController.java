package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.domain.ElasticTyper;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.dto.SearchResponse;
import no.nav.testnav.dollysearchservice.service.OpenSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class OpensearchController {

    private final OpenSearchService openSearchService;

    @GetMapping("/identer")
    @Operation(description = "Henter identer som matcher søk i request, registre kun")
    public Mono<SearchResponse> getIdenterMed(@RequestParam ElasticTyper... typer) {

        return openSearchService.getTyper(typer);
    }

    @PostMapping("/identer")
    @Operation(description = "Henter identer som matcher søk i request, både registre og persondetaljer")
    public Mono<SearchResponse> getIdenterMed(@RequestBody SearchRequest request) {

        return openSearchService.search(request);
    }
}
