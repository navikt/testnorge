package no.nav.registre.testnorge.batchbestillingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.consumer.DollyBackendConsumer;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchBestillingService {

    private final DollyBackendConsumer dollyBackendConsumer;

    public ResponseEntity.BodyBuilder sendBestillinger(Long gruppeId, RsDollyBestillingRequest request, Integer antallPerBatch, Integer antallBatchJobber, Integer delayInMinutes) {

        request.setAntall(antallPerBatch);
        
        var bestillingTimer = new Timer();
        final int[] antallJobberFerdig = { 1 };
        var executeBestillinger = new TimerTask() {

            @Override
            public void run() {
                dollyBackendConsumer.postDollyBestilling(gruppeId, request);
                log.info("antall jobber ferdig {}", antallJobberFerdig[0]);
                antallJobberFerdig[0] += 1;

                if (antallJobberFerdig[0] >= antallBatchJobber) {
                    log.info("Stopper jobb etter {} kjøringer", antallJobberFerdig[0]);
                    bestillingTimer.cancel();
                }
            }
        };

        bestillingTimer.scheduleAtFixedRate(executeBestillinger, 0L, delayInMinutes * (60 * 1000));

        return ResponseEntity.ok();
    }
}
