package no.nav.registre.orkestratoren.consumer.rs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Slf4j
@Component
public class ArenaInntektSyntConsumer {

    private RestTemplate restTemplate;
    private String url;

    public ArenaInntektSyntConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${synthdata-arena-inntekt.rest-api.url}") String baseUrl) {
        this.url = baseUrl + "/v1/generate";
        this.restTemplate = restTemplateBuilder.build();
    }

    public void genererEnInntektsmeldingPerFnrIInntektstub(List<String> fnr) {
        restTemplate.postForObject(url, fnr, String.class);
    }

    @Async
    public void asyncBestillEnInntektsmeldingPerFnrIInntektstub(List<String> inntektsmldMottakere) {
        if (inntektsmldMottakere == null) {
            return;
        }
        LocalDateTime bestillingstidspunktet = LocalDateTime.now();
        try {
            genererEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);
            if (log.isInfoEnabled()) {
                log.info("synth-arena-inntekt har fullført bestillingen som ble sendt {}. " // NOSONAR - Sonar kjenner ikke igjen at log er i if-statement
                        + "Antall inntektsmeldinger opprettet i inntekts-stub: {} ",
                        bestillingstidspunktet, inntektsmldMottakere.size());
            }
        } catch (HttpStatusCodeException e) {
            StringBuilder feilmelding = new StringBuilder(200)
                    .append("synth-arena-inntekt returnerte feilmeldingen ")
                    .append(getMessageFromJson(e.getResponseBodyAsString()))
                    .append(". Bestillingen ble sendt ")
                    .append(bestillingstidspunktet);
            log.error(feilmelding.toString(), e);
        }
    }

    public List<String> startSyntetisering(SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        // kall endepunkt i testnorge-arena-inntekt
        return null;
    }

    private String getMessageFromJson(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(responseBody).findValue("message").asText();
        } catch (IOException e) {
            return responseBody;
        }
    }
}
