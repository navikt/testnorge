package no.nav.registre.sdforvalter.consumer.rs.krr;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.krr.request.KrrRequest;
import no.nav.registre.sdforvalter.domain.Krr;
import no.nav.registre.sdforvalter.domain.KrrListe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KrrConsumer {

    private static final ParameterizedTypeReference<List<KrrRequest>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;
    private final String krrUrl;

    public KrrConsumer(RestClient restClient, @Value("${consumers.krr-stub.url}") String krrUrl) {
        this.restClient = restClient;
        this.krrUrl = krrUrl + "/v1/kontaktinformasjon";
    }

    public Set<String> send(KrrListe liste) {
        Set<String> existing = existingKrrFnr(liste
                .getListe()
                .stream()
                .map(Krr::getFnr)
                .collect(Collectors.toSet())
        );
        List<Krr> noneExisting = liste
                .getListe()
                .stream()
                .filter(krr -> !existing.contains(krr.getFnr()))
                .toList();

        if (noneExisting.isEmpty()) {
            log.info("Fant ingen nye krr.");
            return Collections.emptySet();
        }

        Set<KrrRequest> requestData = noneExisting
                .stream()
                .map(KrrRequest::new)
                .collect(Collectors.toSet());

        Set<String> createdInKrr = new HashSet<>();
        requestData.forEach(d -> {
            try {
                var response = restClient.post()
                        .uri(krrUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Nav-Call-Id", "Synt")
                        .header("Nav-Consumer-Id", "Synt")
                        .body(d)
                        .retrieve()
                        .toBodilessEntity();
                if (response.getStatusCode() != HttpStatus.CREATED) {
                    log.error("Klarte ikke opprette kontaktinformasjon i krr-stub");
                    return;
                }
            } catch (Exception e) {
                log.error("Klarte ikke opprette kontaktinformasjon i krr-stub", e);
                return;
            }
            createdInKrr.add(d.getFnr());
        });
        return createdInKrr;
    }


    private Set<String> existingKrrFnr(Set<String> fnrs) {
        Set<String> existing = new HashSet<>();

        fnrs.forEach(fnr -> {
            try {
                var response = restClient.get()
                        .uri(krrUrl)
                        .header("Nav-Call-Id", "Synt")
                        .header("Nav-Consumer-Id", "Synt")
                        .header("Nav-Personident", fnr)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(RESPONSE_TYPE);
                if (response != null) {
                    existing.addAll(response.stream().map(KrrRequest::getFnr).collect(Collectors.toSet()));
                }
            } catch (HttpClientErrorException ignored) {
                log.info("Fant ikke person {}", fnr);
            }
        });

        return existing;
    }
}
