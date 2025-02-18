package no.nav.dolly.provider.api;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.service.DollySearchService;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opensearch")
@RequiredArgsConstructor
public class DollySearchController {

    private final DollySearchService dollySearchService;

    @PostMapping
    public Mono<SearchResponse> searchPersoner(@RequestParam(required = false) List<ElasticTyper> registre,
                                              @RequestBody SearchRequest request) {

        return dollySearchService.search(registre, request);
    }
}
