package no.nav.registre.orkestratoren.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;
import no.nav.registre.orkestratoren.service.IdentService;

@Slf4j
@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @Value("#{${batch.avspillergruppeId.miljoe}}")
    private Map<Long, String> avspillergruppeIdMedMiljoe;

    @LogExceptions
    @DeleteMapping
    public SlettedeIdenterResponse slettIdenterFraAdaptere(
            @RequestParam Long avspillergruppeId,
            @RequestParam String miljoe,
            @RequestParam String testdataEier,
            @RequestBody List<String> identer) {
        return identService.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);
    }

    @LogExceptions
    @PostMapping("/synkronisering")
    @Scheduled(cron = "0 0 1 1 * *")
    public Map<Long, List<String>> synkroniserMedTps() {
        Map<Long, List<String>> avspillergruppeMedSlettedeIdenter = new HashMap<>();
        for (Map.Entry<Long, String> entry : avspillergruppeIdMedMiljoe.entrySet()) {
            List<String> newEntry = avspillergruppeMedSlettedeIdenter.put(entry.getKey(), new ArrayList<>());
            if (newEntry != null) {
                newEntry.addAll(identService.synkroniserMedTps(entry.getKey(), entry.getValue()));
            }
        }
        return avspillergruppeMedSlettedeIdenter;
    }
}
