package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.service.OpenSearchV2Service;
import no.nav.testnav.libs.data.dollysearchservice.v2.ElasticTyper;
import no.nav.testnav.libs.data.dollysearchservice.v2.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v2.SearchResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/opensearch")
@RequiredArgsConstructor
public class OpensearchEnhancedController {

    private final OpenSearchV2Service openSearchV2Service;

    @PostMapping
    @Operation(description = "Henter personer som matcher søk av persondetaljer i request")
    public Mono<SearchResponse> getPersoner(@RequestParam(required = false) List<ElasticTyper> registreRequest,
                                            @RequestBody SearchRequest request) {

        return openSearchV2Service.search(request, registreRequest);
    }
}
