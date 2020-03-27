package no.nav.registre.sdForvalter.consumer.rs.ereg;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import no.nav.registre.sdForvalter.consumer.rs.response.ereg.OrganisasjonResponse;
import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;

@Slf4j
public class AsyncOrganisasjonMap {
    private final Map<String, CompletableFuture<OrganisasjonResponse>> futureList = new HashMap<>();

    public void put(String orgnummer, CompletableFuture<OrganisasjonResponse> completable) {
        futureList.put(orgnummer, completable);
    }

    public Map<String, Organisasjon> getMap() {
        Map<String, Organisasjon> organisasjoner = new HashMap<>();
        futureList.forEach((orgnummer, completable) -> {
            try {
                OrganisasjonResponse response = completable.get();
                if (response != null) {
                    Organisasjon organisasjon = response.toOrganisasjon();
                    if (!orgnummer.equals(organisasjon.getOrgnummer())) {
                        throw new RuntimeException(
                                "Miss match mellom orgnummer fra faste data " + orgnummer + " og ereg " + organisasjon.getOrgnummer()
                        );
                    }
                    organisasjoner.put(organisasjon.getOrgnummer(), organisasjon);
                }
            } catch (Exception e) {
                log.error("Klarte ikke å hente ut {} fra ereg", orgnummer, e);
            }
        });
        return organisasjoner;
    }
}
