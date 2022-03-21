package no.nav.dolly.bestilling.aktoeridsyncservice;

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
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@Service
@Order(4)
@RequiredArgsConstructor
public class AktoerIdSyncClient implements ClientRegister {

    private static final int MAX_COUNT = 200;
    private static final int TIMEOUT = 50;
    private static final int ELAPSED = 10;

    private final AktoerIdSyncConsumer personServiceConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var count = 0;

        var startTime = now();
        try {
            while (count++ < MAX_COUNT && ChronoUnit.SECONDS.between(startTime, now()) > ELAPSED &&
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
            log.info("Synkronisering mot PersonService (AktoerId) tok {} ms.", ChronoUnit.MILLIS.between(startTime, now()));
        } else {
            log.warn("Synkronisering mot PersonService (AktoerId) gitt opp etter {} ms.",
                    ChronoUnit.MILLIS.between(startTime, now()));
        }
    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }
}
