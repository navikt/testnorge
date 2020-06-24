package no.nav.registre.sdforvalter.consumer.rs.ereg;

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

import no.nav.registre.sdforvalter.consumer.rs.ereg.command.GetOrganisasjon;
import no.nav.registre.sdforvalter.consumer.rs.response.ereg.EregOrganisasjon;
import no.nav.registre.sdforvalter.domain.status.ereg.Organisasjon;


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

    private CompletableFuture<EregOrganisasjon> getOrganisasjon(String orgnummer, String miljo, Executor executor) {
        return CompletableFuture.supplyAsync(
                () -> new GetOrganisasjon(eregUrl, miljo, orgnummer, restTemplate).call(),
                executor
        );
    }

    public Map<String, Organisasjon> getOrganisasjoner(List<String> orgnummerList, String miljo) {
        log.info("Henter ut {} fra ereg", String.join(", ", orgnummerList));
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        AsyncOrganisasjonMap asyncMap = new AsyncOrganisasjonMap();
        orgnummerList.forEach(orgnummer -> asyncMap.put(orgnummer, getOrganisasjon(orgnummer, miljo, executorService)));
        return asyncMap.getMap();
    }
}

