package no.nav.dolly.provider.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchRequest;
import no.nav.dolly.elastic.service.ElasticsearchSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class ElasticController {

    private final ElasticsearchSearchService elasticsearchSearchService;

    @GetMapping("/all")
    public List<ElasticBestilling> getAll() {

        return elasticsearchSearchService.getAll();
    }

    @GetMapping("/typer")
    public List<String> getIdenterMed(@RequestParam ElasticTyper... typer) {

        return elasticsearchSearchService.getTyper(typer);
    }

    @PostMapping()
    public List<String> getIdenterMed(@RequestBody SearchRequest request) {

        return elasticsearchSearchService.search(request);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wrapper {

        private RsDollyBestilling dollyBestilling;
        private Bestilling bestillingStatus;
    }
}
