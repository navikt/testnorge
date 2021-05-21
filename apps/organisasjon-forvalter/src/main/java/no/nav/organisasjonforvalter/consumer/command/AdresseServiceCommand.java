package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static java.lang.String.format;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@RequiredArgsConstructor
public class AdresseServiceCommand implements Callable<VegadresseDTO[]> {

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
    public VegadresseDTO[] call() {

        return isNotBlank(postnummer) || isNotBlank(kommunenummer) ?

                webClient.get()
                        .uri(format("%s%s", ADRESSE_URL, getSuffix(postnummer, kommunenummer)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(VegadresseDTO[].class)
                        .block() :

                webClient.get()
                        .uri(format("%s/auto", ADRESSE_URL))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(VegadresseDTO[].class)
                        .block();
    }
}
