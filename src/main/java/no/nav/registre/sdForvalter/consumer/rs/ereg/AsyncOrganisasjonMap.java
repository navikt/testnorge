package no.nav.registre.sdForvalter.consumer.rs.ereg;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import no.nav.registre.sdForvalter.consumer.rs.response.ereg.OrganisasjonResponse;
import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;

@Slf4j
public class AsyncOrganisasjonMap {
    private final List<CompletableFuture<OrganisasjonResponse>> futureList = new ArrayList<>();

    public void add(CompletableFuture<OrganisasjonResponse> completable) {
        futureList.add(completable);
    }

    public Map<String, Organisasjon> getMap() {
        Map<String, Organisasjon> organisasjoner = new HashMap<>();
        futureList.forEach(completable -> {
            try {
                OrganisasjonResponse response = completable.get();
                if(response != null){
                    Organisasjon organisasjon = new Organisasjon(response);
                    organisasjoner.put(organisasjon.getOrgnummer(), organisasjon);
                }
            } catch (Exception e) {
                log.error("Klarte ikke å hente ut organisasjon fra ereg", e);
            }
        });
        return organisasjoner;
    }
}
