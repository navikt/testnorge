package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.service.PersonerSearchService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonerSearchController {

    private final PersonerSearchService personerSearchService;

    @PostMapping
    @Operation(description = "Henter Dolly-personer som matcher både søk i registre og søk av persondetaljer i PDL")
    public Mono<SearchResponse> getPersoner(@RequestParam(required = false) List<ElasticTyper> registreRequest,
                                            @RequestBody SearchRequest request) {

        return personerSearchService.search(request, registreRequest);
    }

    @GetMapping("/typer")
    @Operation(description = "Henter alle søketyper mot registre")
    public Mono<List<Kategori>> getKategorier() {

        return Mono.just(personerSearchService.getTyper());
    }
}
