package no.nav.testnav.identpool.providers.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.ajourhold.BatchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping(value = "/startmining")
    public Flux<String> startMiningBatch() {

        return batchService.startGeneratingIdents();
    }

    @PostMapping("/startprodclean")
    public void startProdCleanBatch() {

        batchService.updateDatabaseWithProdStatus();
    }
}
