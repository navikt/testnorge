package no.nav.registre.sdforvalter.consumer.rs.organisasjon;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.sdforvalter.domain.status.ereg.Organisasjon;
import no.nav.registre.sdforvalter.exception.MapMismatchException;

@Slf4j
public class AsyncOrganisasjonMap {
    private final Map<String, CompletableFuture<Organisasjon>> futureList = new HashMap<>();

    public void put(String orgnummer, CompletableFuture<Organisasjon> completable) {
        futureList.put(orgnummer, completable);
    }

    public Map<String, Organisasjon> getMap() {
        Map<String, Organisasjon> organisasjoner = new HashMap<>();
        futureList.forEach((orgnummer, completable) -> {
            try {
                Organisasjon organisasjon = completable.get();
                if (organisasjon != null) {
                    if (!orgnummer.equals(organisasjon.getOrgnummer())) {
                        throw new MapMismatchException(
                                "Miss match mellom orgnummer fra faste data " + orgnummer + " og ereg " + organisasjon.getOrgnummer()
                        );
                    }
                    organisasjoner.put(organisasjon.getOrgnummer(), organisasjon);
                }
            } catch (Exception e) {
                log.error("Klarte ikke å hente ut {} fra ereg", orgnummer, e);
                Thread.currentThread().interrupt();
            }
        });
        return organisasjoner;
    }
}
