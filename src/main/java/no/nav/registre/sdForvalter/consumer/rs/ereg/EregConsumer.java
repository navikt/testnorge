package no.nav.registre.sdForvalter.consumer.rs.ereg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private CompletableFuture<OrganisasjonResponse> getOrganisasjon(String orgnummer, String miljo, Executor executor) {
        return CompletableFuture.supplyAsync(
                () -> new GetOrganisasjon(eregUrl, miljo, orgnummer, restTemplate).call(),
                executor
        );
    }

    public Map<String, Organisasjon> getOrganisasjoner(List<String> orgnummerList, String miljo) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        AsyncOrganisasjonMap asyncMap = new AsyncOrganisasjonMap();
        orgnummerList.forEach(orgnummer -> asyncMap.put(orgnummer, getOrganisasjon(orgnummer, miljo, executorService)));
        return asyncMap.getMap();
    }
}

