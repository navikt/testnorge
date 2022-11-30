package no.nav.testnav.apps.personservice.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.PdlApiConsumer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlSyncService {

    private static final long ELAPSED_S = 30;
    private static final long TIME_TO_LIVE_S = 60;
    private static final long SLEEP_MS = 100;

    private final PdlApiConsumer pdlApiConsumer;
    private Map<String, IdentStatus> identerStatus = new ConcurrentHashMap<>();

    public Boolean syncPdlPersonReady(String ident) {

        if (identerStatus.containsKey(ident) &&
                ChronoUnit.SECONDS.between(LocalDateTime.now(),
                        identerStatus.get(ident).getAvailStartTime()) < TIME_TO_LIVE_S) {

            log.info("Eksisterende ident {} isReady {}", ident, identerStatus.get(ident).isReady);
            return identerStatus.get(ident).isReady;
        } else {

            if (!identerStatus.containsKey(ident) || isNull(identerStatus.get(ident).getRequestStartTime())) {
                checkAndUpdateIdenterStatus(ident);
            }
            synchronized (identerStatus.get(ident)) {
                while (isNull(identerStatus.get(ident).getIsReady())) {
                    try {
                        identerStatus.get(ident).wait();
                    } catch (InterruptedException e) {
                        log.error("Interrupted exception {}", e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        log.info("Eksisterende ident {} isReady {}", ident, identerStatus.get(ident).isReady);
        return identerStatus.get(ident).isReady;
    }

    @Synchronized
    private void checkAndUpdateIdenterStatus(String ident) {

        if (!identerStatus.containsKey(ident) || isNull(identerStatus.get(ident).getRequestStartTime())) {
            identerStatus.put(ident, IdentStatus.builder()
                    .requestStartTime(LocalDateTime.now())
                    .build());

            log.info("Synk av ident {} ble startet {}", ident, LocalDateTime.now());
            synchPdlPerson(ident);
        }
    }

    @Async
    protected void synchPdlPerson(String ident) {

        var startTime = now();
        boolean isPerson = false;
        try {
            while (ChronoUnit.SECONDS.between(startTime, now()) < ELAPSED_S &&
                    !(isPerson = pdlApiConsumer.isPerson(ident).block())) {
                Thread.sleep(SLEEP_MS);
            }

        } catch (InterruptedException e) {
            log.error("Sync mot PersonService (isPerson) ble avbrutt.", e);
            Thread.currentThread().interrupt();

        } catch (RuntimeException e) {
            log.error("Feilet å sjekke om person finnes for ident {}.", ident, e);

        } finally {

            synchronized (identerStatus.get(ident)) {
                identerStatus.get(ident).setAvailStartTime(now());
                identerStatus.get(ident).setRequestStartTime(null);
                identerStatus.get(ident).setIsReady(isPerson);

                identerStatus.get(ident).notifyAll();
            }
            log.info("Synk av ident {} med status {} har klar tid {}", ident, isPerson, LocalDateTime.now());
        }

        if (ChronoUnit.SECONDS.between(startTime, now()) < ELAPSED_S) {
            log.info("Synkronisering mot PDL (isPerson) tok {} ms.", ChronoUnit.MILLIS.between(startTime, now()));
        } else {
            log.error("Synkronisering mot PDL (isPerson) gitt opp etter {} ms.",
                    ChronoUnit.MILLIS.between(startTime, now()));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class IdentStatus {

        private LocalDateTime requestStartTime;
        private LocalDateTime availStartTime;
        private Boolean isReady;
    }
}
