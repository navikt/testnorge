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
import no.nav.dolly.service.OpenSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class ElasticController {

    private final OpenSearchService openSearchService;

    @GetMapping("/all")
    public List<ElasticBestilling> getAll() {

        return openSearchService.getAll();
    }

    @GetMapping("/typer")
    public List<String> getIdenterMed(ElasticTyper... typer) {

        return openSearchService.getTyper(typer);
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
