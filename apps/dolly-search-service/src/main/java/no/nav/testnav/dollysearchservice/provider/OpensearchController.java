package no.nav.testnav.dollysearchservice.provider;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.domain.ElasticTyper;
import no.nav.testnav.dollysearchservice.domain.jpa.BestillingElasticRepository;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.dto.SearchResponse;
import no.nav.testnav.dollysearchservice.service.OpenSearchService;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticBestilling;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class OpensearchController {

    private final OpenSearchService openSearchService;
    private final BestillingElasticRepository bestillingElasticRepository;

    @GetMapping("/bestilling/ident/{ident}")
    @Operation(description = "Henter all lagret informasjon basert på ident")
    public List<ElasticBestilling> getAll(@PathVariable String ident) {

        return openSearchService.search(ident);
    }

    @GetMapping("/typer")
    @Operation(description = "Henter alle typer som kan søkes på")
    public List<Kategori> getKategorier() {

        return openSearchService.getTyper();
    }

    @GetMapping("/identer")
    @Operation(description = "Henter identer som matcher søk i request, registre kun")
    public SearchResponse getIdenterMed(@RequestParam ElasticTyper... typer) {

//        return openSearchService.getTyper(typer);
        return null;
    }

    @PostMapping("/identer")
    @Operation(description = "Henter identer som matcher søk i request, både registre og persondetaljer")
    public SearchResponse getIdenterMed(@RequestBody SearchRequest request) {

//        return openSearchService.search(request);
        return null;
    }

    @GetMapping("/bestilling/id/{id}")
    @Operation(description = "Henter lagret bestilling, basert på id")
    public ElasticBestilling getBestillinger(@PathVariable long id) {

        return bestillingElasticRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("Bestilling med id: %d finnes ikke", id)));
    }
}
