package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.dto.responses.AdresseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static java.lang.String.format;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@RequiredArgsConstructor
public class AdresseServiceCommand implements Callable<AdresseResponse> {

    private static final String ADRESSE_URL = "/api/v1/adresser";

    private final WebClient webClient;
    private final String postnummer;
    private final String kommunenummer;
    private final String token;

    public static String getSuffix(String postnr, String kommunenr) {

        if (isNotBlank(postnr)) {
            return "/postnummer/" + postnr;
        } else {
            return "/kommunenummer/" + kommunenr;
        }
    }

    @Override
    public AdresseResponse call() {

        return isNotBlank(postnummer) || isNotBlank(kommunenummer) ?

                webClient.get()
                        .uri(format("%s%s", ADRESSE_URL, getSuffix(postnummer, kommunenummer)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(AdresseResponse.class)
                        .block() :

                webClient.post()
                        .uri(format("%s/auto", ADRESSE_URL))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("{}"))
                        .retrieve()
                        .bodyToMono(AdresseResponse.class)
                        .block();
    }
}
