package no.nav.registre.testnorge.batchbestillingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.consumer.DollyBackendConsumer;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchBestillingService {

    private final DollyBackendConsumer dollyBackendConsumer;

    public void sendBestillinger(Long gruppeId, RsDollyBestillingRequest request, Long antallPerBatch, Integer antallBatchJobber, Integer delayInMinutes, Boolean sendToProd) {

        var bestillingTimer = new Timer();
        final int[] antallJobberFerdig = { 0 };
        var executeBestillinger = new TimerTask() {

            @Override
            public void run() {

                if (!dollyBackendConsumer.getAktiveBestillinger(gruppeId, sendToProd).isEmpty()) {
                    log.warn("Gruppe {} har aktive bestillinger kjørende, venter {} minutter før neste kjøring.", gruppeId, delayInMinutes);
                    return;
                }

                dollyBackendConsumer.postDollyBestilling(gruppeId, request, antallPerBatch, sendToProd);
                antallJobberFerdig[0] += 1;
                log.info("antall jobber ferdig {}/{}", antallJobberFerdig[0], antallBatchJobber);

                if (antallJobberFerdig[0] >= antallBatchJobber) {
                    log.info("Stopper batchjobb etter {} kjøringer", antallJobberFerdig[0]);
                    bestillingTimer.cancel();
                }
            }
        };

        bestillingTimer.scheduleAtFixedRate(executeBestillinger, 0L, delayInMinutes * (60 * 1000));
    }
}
