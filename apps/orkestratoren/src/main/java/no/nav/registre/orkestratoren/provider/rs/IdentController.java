package no.nav.registre.orkestratoren.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;
import no.nav.registre.orkestratoren.service.IdentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ident")
public class IdentController {

    private final IdentService identService;

    @Value("${batch.avspillergruppeId}")
    private long avspillergruppeId;

    @Value("${batch.miljoe)")
    private String miljoe;

    @DeleteMapping
    public SlettedeIdenterResponse slettIdenterFraAdaptere(
            @RequestParam Long avspillergruppeId,
            @RequestParam String miljoe,
            @RequestParam String testdataEier,
            @RequestBody List<String> identer
    ) {
        return identService.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);
    }

    @PostMapping("/synkronisering")
    @Scheduled(cron = "0 0 1 1 * *")
    public Map<Long, SlettedeIdenterResponse> synkroniserMedTps() {
        Map<Long, SlettedeIdenterResponse> avspillergruppeMedFjernedeIdenter = new HashMap<>();
        avspillergruppeMedFjernedeIdenter.put(avspillergruppeId, identService.synkroniserMedTps(avspillergruppeId, miljoe));

        return avspillergruppeMedFjernedeIdenter;
    }

    @PostMapping("/rensAvspillergruppe")
    @Scheduled(cron = "0 0 2 * * *")
    public Map<Long, SlettedeIdenterResponse> rensAvspillergruppe() {
        Map<Long, SlettedeIdenterResponse> avspillergruppeMedFjernedeIdenter = new HashMap<>();
        avspillergruppeMedFjernedeIdenter.put(avspillergruppeId, identService.fjernKolliderendeIdenter(avspillergruppeId, miljoe));
        return avspillergruppeMedFjernedeIdenter;
    }
}
