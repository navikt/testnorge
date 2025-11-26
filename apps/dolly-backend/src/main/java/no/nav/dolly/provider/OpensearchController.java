package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.elastic.service.OpenSearchService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@RestController
@RequestMapping("/api/v1/opensearch")
@RequiredArgsConstructor
public class OpensearchController {

    private final OpenSearchService openSearchService;

    @DeleteMapping("/bestilling/id/{id}")
    @Operation(description = "Sletter søkbar bestilling, basert på id")
    public Mono<String> deleteBestilling(@PathVariable long id) {

        return openSearchService.exists(id)
                .flatMap(exists -> {

                    if (isTrue(exists)) {
                        return openSearchService.deleteById(id);

                    } else {
                        return Mono.just("Bestilling med id: " + id + " finnes ikke");
                    }
                });
    }

    @DeleteMapping()
    @Operation(description = "Sletter all data inkludert indeks")
    public Mono<String> delete() {

         return openSearchService.deleteIndex();
    }
}