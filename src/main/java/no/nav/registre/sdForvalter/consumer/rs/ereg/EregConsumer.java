package no.nav.registre.sdForvalter.consumer.rs.ereg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import no.nav.registre.sdForvalter.consumer.rs.ereg.command.GetOrganisasjon;
import no.nav.registre.sdForvalter.consumer.rs.response.ereg.OrganisasjonResponse;
import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;


@Slf4j
@Component
public class EregConsumer {
    private final RestTemplate restTemplate;
    private final String eregUrl;

    public EregConsumer(
            RestTemplate restTemplate,
            @Value("${ereg.api}") String eregUrl
    ) {
        this.restTemplate = restTemplate;
        this.eregUrl = eregUrl;
    }

    private CompletableFuture<OrganisasjonResponse> getOrganisasjon(String orgnummer, String miljo) {
        return CompletableFuture.supplyAsync(() -> new GetOrganisasjon(eregUrl, orgnummer, miljo, restTemplate).call());
    }

    public Map<String, Organisasjon> getOrganisasjoner(List<String> orgnummerList, String miljo) {
        AsyncOrganisasjonMap list = new AsyncOrganisasjonMap();
        orgnummerList.forEach(orgnummer -> list.add(getOrganisasjon(orgnummer, miljo)));
        return list.getMap();
    }
}

