package no.nav.dolly.bestilling.aktoerregister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.containsSynthEnv;

@Slf4j
@Service
@Order(3)
@RequiredArgsConstructor
public class AktoerregisterClient implements ClientRegister {

    private static final int MAX_COUNT = 200;
    private static final int TIMEOUT = 50;

    private final AktoerregisterConsumer aktoerregisterConsumer;

    @Override public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (containsSynthEnv(bestilling.getEnvironments())) {
            int count = 0;

            try {
                while (count++ < MAX_COUNT &&
                        isNull(aktoerregisterConsumer.getAktoerId(dollyPerson.getHovedperson())
                                .get(dollyPerson.getHovedperson()).get("identer"))) {
                    Thread.sleep(TIMEOUT);
                }

            } catch (InterruptedException e) {
                log.error("Sync mot Aktørregister ble avbrutt.", e);
                Thread.currentThread().interrupt();

            } catch (RuntimeException e) {
                log.error("Feilet å lese id fra Aktørregister for ident {}.", dollyPerson.getHovedperson(), e);
            }

            if (count < MAX_COUNT) {
                log.info("Synkronisering mot Aktørregister tok {} ms.", count * TIMEOUT);
            } else {
                log.warn("Synkronisering mot Aktørregister gitt opp etter {} ms.", MAX_COUNT * TIMEOUT);
            }
        }
    }

    @Override public void release(List<String> identer) {

    }

    @Override
    public boolean isTestnorgeRelevant() {
        return false;
    }
}
