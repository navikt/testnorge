package no.nav.registre.sdforvalter.consumer.rs;

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

import no.nav.registre.sdforvalter.domain.status.ereg.Organisasjon;
import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;

@Slf4j
@Component
public class OrganisasjonConsumer {
    private final RestTemplate restTemplate;
    private final String url;
    private final Executor executor;

    public OrganisasjonConsumer(
            RestTemplate restTemplate,
            @Value("${organsisasjon.api.url}") String url,
            @Value("${organsisasjon.api.threads}") Integer threads
    ) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    private CompletableFuture<Organisasjon> getOrganisasjon(String orgnummer, String miljo, Executor executor) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return new Organisasjon(new GetOrganisasjonCommand(restTemplate, url, orgnummer, miljo).call());
                    } catch (Exception e) {
                        log.warn("Klarer ikke å hente organsisasjon  {}", orgnummer);
                        return null;
                    }
                },
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
