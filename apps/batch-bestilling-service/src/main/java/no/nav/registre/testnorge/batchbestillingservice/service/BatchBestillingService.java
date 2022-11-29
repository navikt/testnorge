package no.nav.registre.testnorge.batchbestillingservice.service;

import no.nav.registre.testnorge.batchbestillingservice.consumer.DollyBackendConsumer;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class BatchBestillingService {

    DollyBackendConsumer dollyBackendConsumer;

    public ResponseEntity.BodyBuilder sendBestillinger(Long gruppeId, RsDollyBestillingRequest request, Long antallPerBatch, Integer antallBatchJobber, Integer delayInMinutes) {

        var bestillingTimer = new Timer();
        final int[] antallJobberFerdig = { 0 };
        var executeBestillinger = new TimerTask() {

            @Override
            public void run() {
                if (antallJobberFerdig[0] >= antallBatchJobber) {
                    bestillingTimer.cancel();
                }
                dollyBackendConsumer.postDollyBestilling(gruppeId, request, antallPerBatch);
                antallJobberFerdig[0] += 1;
            }
        };

        bestillingTimer.scheduleAtFixedRate(executeBestillinger, 0L, delayInMinutes * (60 * 1000));

        return ResponseEntity.ok();
    }
}
