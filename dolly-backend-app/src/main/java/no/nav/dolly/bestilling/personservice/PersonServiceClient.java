package no.nav.dolly.bestilling.personservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.dolly.domain.CommonKeysAndUtils.containsSynthEnv;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@Service
@Order(3)
@RequiredArgsConstructor
public class PersonServiceClient implements ClientRegister {

    private static final int MAX_COUNT = 200;
    private static final int TIMEOUT = 50;

    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (containsSynthEnv(bestilling.getEnvironments())) {
            int count = 0;

            try {
                while (count++ < MAX_COUNT &&
                        isBlank(personServiceConsumer.getAktoerId(dollyPerson.getHovedperson()).getIdent())) {
                    Thread.sleep(TIMEOUT);
                }

            } catch (InterruptedException e) {
                log.error("Sync mot PersonService (AktoerId) ble avbrutt.", e);
                Thread.currentThread().interrupt();

            } catch (RuntimeException e) {
                log.error("Feilet å lese id fra PersonService (AktoerId) for ident {}.", dollyPerson.getHovedperson(), e);
            }

            if (count < MAX_COUNT) {
                log.info("Synkronisering mot PersonService (AktoerId) tok {} ms.", count * TIMEOUT);
            } else {
                log.warn("Synkronisering mot PersonService (AktoerId) gitt opp etter {} ms.", MAX_COUNT * TIMEOUT);
            }
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    @Override
    public boolean isTestnorgeRelevant() {
        return false;
    }
}
