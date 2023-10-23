package no.nav.dolly.provider.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.service.ElasticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/elastic")
@RequiredArgsConstructor
public class ElasticController {

    private final ElasticService elasticService;

    @Deprecated
    @PostMapping
    public void lagreBestilling(@RequestBody Wrapper bestilling) {

        elasticService.lagreBestillingMedStatus(bestilling.getDollyBestilling(), bestilling.getBestillingStatus());
    }

    @GetMapping
    public List<String> getIdenter() {

        return null;
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
