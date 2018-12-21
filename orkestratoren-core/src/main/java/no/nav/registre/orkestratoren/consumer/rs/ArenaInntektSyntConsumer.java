package no.nav.registre.orkestratoren.consumer.rs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArenaInntektSyntConsumer {

    RestTemplate restTemplate;
    private String url;

    public ArenaInntektSyntConsumer(@Value("${synthdata-arena-inntekt.rest-api.url}") String baseUrl) {
        this.url = baseUrl + "/v1/generate";
        this.restTemplate = new RestTemplate();
    }

    public void genererEnInntektsmeldingPerFnrIInntektstub(List<String> fnr) {
        restTemplate.postForObject(url, fnr, String.class);
    }

    @Async
    public void asyncBestillEnInntektsmeldingPerFnrIInntektstub(List<String> inntektsmldMottakere) {
        LocalDateTime bestillingstidspunktet = LocalDateTime.now();
        try {
            genererEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);
            log.info("synth-arena-inntekt har fullført bestillingen som ble sendt {}. "
                    + "Antall inntektsmeldinger opprettet i inntekts-stub: {} ", bestillingstidspunktet, inntektsmldMottakere.size());
        } catch (HttpStatusCodeException e) {
            StringBuilder feilmelding = new StringBuilder();
            feilmelding.append("synth-arena-inntekt returnerte feilmeldingen ");
            feilmelding.append(getMessageFromJson(e.getResponseBodyAsString()));
            feilmelding.append(". Bestillingen ble sendt ");
            feilmelding.append(bestillingstidspunktet);
            log.error(feilmelding.toString(), e);
        }
    }

    private String getMessageFromJson(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(responseBody).findValue("message").asText();
        } catch (IOException e) {
            return responseBody;
        }
    }
}
