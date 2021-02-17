package no.nav.identpool.rs.v1;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ajourhold.BatchService;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
@Profile("local")
public class BatchController {

    private final BatchService batchService;

    @PostMapping("/startmining")
    public void startMiningBatch() {

        batchService.startGeneratingIdentsBatch();
    }

    @PostMapping("/startprodclean")
    public void startProdCleanBatch() {

        batchService.updateDatabaseWithProdStatus();
    }
}
