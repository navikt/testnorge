package no.nav.registre.testnorge.batchbestillingservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import no.nav.registre.testnorge.batchbestillingservice.service.BatchBestillingService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchBestillingService batchBestillingService;

    @PostMapping("/{gruppeId}/{antallIdenterPerBatch}")
    @Operation(description = "Send batch bestilling til Dolly backend med mulighet for spesifisert antall minutter pause mellom hver kjøring. " +
            "Krever gruppeId og antall identer per batchkjøring. " +
            "Går mot Dolly-backend-dev som standard, med mulighet for Dolly-backend")
    public void sendBatchBestilling(@RequestBody RsDollyBestillingRequest request,
                                    @PathVariable("gruppeId") Long gruppeId,
                                    @PathVariable("antallIdenterPerBatch") Long antallIdenterPerBatch,
                                    @RequestParam(value = "antallMinutterPause", defaultValue = "5", required = false) Integer delayInMinutes,
                                    @RequestParam(value = "antallBatchJobber", defaultValue = "10", required = false) Integer antallBatchJobber
    ) {

        batchBestillingService.sendBestillinger(gruppeId, request, antallIdenterPerBatch, antallBatchJobber, delayInMinutes);
    }
}