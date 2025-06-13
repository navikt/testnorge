package no.nav.testnav.identpool.providers.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.ajourhold.BatchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping(value = "/start-mining")
    public Mono<String> startMiningBatch(@RequestParam(required = false) Integer yearToGenerate) {

        return batchService.startGeneratingIdents(yearToGenerate)
                .map(ajourhold -> isNotBlank(ajourhold.getMelding()) ?
                        ajourhold.getMelding() : ajourhold.getFeilmelding());
    }

    @PostMapping(value = "/start-prod-clean")
    public Mono<String> startProdCleanBatch(@RequestParam(required = false) Integer yearToClean) {

        return batchService.updateDatabaseWithProdStatus(yearToClean)
                .map(ajourhold -> isNotBlank(ajourhold.getMelding()) ?
                        ajourhold.getMelding() : ajourhold.getFeilmelding());
    }
}
