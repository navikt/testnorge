package no.nav.dolly.provider;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.service.OpenSearchService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class OpensearchController {

    private final OpenSearchService openSearchService;
    private final BestillingElasticRepository bestillingElasticRepository;

    @DeleteMapping("/bestilling/id/{id}")
    @Operation(description = "Sletter søkbar bestilling, basert på id")
    public Mono<Void> deleteBestilling(@PathVariable long id) {

        return bestillingElasticRepository.deleteById(id);
    }

    @DeleteMapping()
    @Operation(description = "Sletter all data inkludert indeks")
    public Mono<JsonNode> delete() {

        return bestillingElasticRepository.deleteAll()
                .then(openSearchService.deleteIndex());
    }
}