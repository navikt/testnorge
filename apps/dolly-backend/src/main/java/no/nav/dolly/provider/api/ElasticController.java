package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchRequest;
import no.nav.dolly.elastic.dto.SearchResponse;
import no.nav.dolly.elastic.service.ElasticsearchSearchService;
import no.nav.dolly.exceptions.NotFoundException;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class ElasticController {

    private final ElasticsearchSearchService elasticsearchSearchService;
    private final BestillingElasticRepository bestillingElasticRepository;

    @PostMapping("/search-hits")
    @Operation(description = "Henter all lagret informasjon i hht request")
    public SearchHits<ElasticBestilling> getAll(@RequestBody(required = false) SearchRequest request) {

        return elasticsearchSearchService.getAll(request);
    }

    @GetMapping("/search-hits/ident/{ident}")
    @Operation(description = "Henter all lagret informasjon basert på ident")
    public SearchHits<ElasticBestilling> getAll(@PathVariable String ident) {

        return elasticsearchSearchService.getAll(ident);
    }

    @GetMapping("/identer")
    @Operation(description = "Henter identer som matcher søk i request, registre kun")
    public SearchResponse getIdenterMed(@RequestParam ElasticTyper... typer) {

        return elasticsearchSearchService.getTyper(typer);
    }

    @PostMapping("/identer")
    @Operation(description = "Henter identer som matcher søk i request, både registre og persondetaljer")
    public SearchResponse getIdenterMed(@RequestBody SearchRequest request) {

        return elasticsearchSearchService.search(request);
    }

    @GetMapping("/bestilling/id/{id}")
    @Operation(description = "Henter lagret bestilling, basert på id")
    public ElasticBestilling getBestillinger(@PathVariable long id) {

        return bestillingElasticRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(format("Bestilling med id: %d finnes ikke", id)));
    }

    @DeleteMapping("/bestilling/id/{id}")
    @Operation(description = "Sletter søkbar bestilling, basert på id")
    public void deleteBestilling(@PathVariable long id) {

        bestillingElasticRepository.deleteById(id);
    }

    @DeleteMapping()
    @Operation(description = "Sletter all data")
    public void delete() {

        bestillingElasticRepository.deleteAll();
    }
}
