package no.nav.dolly.bestilling.personservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.LocalDateTime.now;

@Slf4j
@Service
@Order(5)
@RequiredArgsConstructor
public class PersonServiceClient implements ClientRegister {

    private static final int MAX_COUNT = 200;
    private static final int TIMEOUT = 50;
    private static final int ELAPSED = 10;

    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var count = 0;

        var startTime = now();
        boolean isPerson = false;
        try {
            while (count++ < MAX_COUNT && ChronoUnit.SECONDS.between(startTime, now()) < ELAPSED &&
                    !(isPerson = personServiceConsumer.isPerson(dollyPerson.getHovedperson()))) {
                Thread.sleep(TIMEOUT);
            }

        } catch (InterruptedException e) {
            log.error("Sync mot PersonService (isPerson) ble avbrutt.", e);
            Thread.currentThread().interrupt();

        } catch (RuntimeException e) {
            log.error("Feilet å sjekke om person finnes for ident {}.", dollyPerson.getHovedperson(), e);

        } finally {

            dollyPerson.setOpprettetIPDL(isPerson);
        }

        if (count < MAX_COUNT && ChronoUnit.SECONDS.between(startTime, now()) < ELAPSED) {
            log.info("Synkronisering mot PersonService (isPerson) tok {} ms.", ChronoUnit.MILLIS.between(startTime, now()));
        } else {
            log.error("Synkronisering mot PersonService (isPerson) gitt opp etter {} ms.",
                    ChronoUnit.MILLIS.between(startTime, now()));
        }

    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }

    public Object status() {
        return personServiceConsumer.checkStatus();
    }
}
