package no.nav.testnorge.apps.levendearbeidsforholdansettelse.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnorge.apps.levendearbeidsforholdansettelse.domain.tenor.TenorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
public class HentPersonerCommand {
    private static final String PATH = "/api/v1/tenor/testdata/raw";
    private final String token;
    private final WebClient webClient;

    public JsonNode hentPersonData(String kommunenr){
        System.out.println("Henter personer");
        var request = webClient
                .get()
                .uri(builder -> builder
                        .path(PATH)
                        .queryParam("type", "Spesifikt")
                        .queryParam("fields", "id", "kommunenr", "arbeidsforhold")
                        .queryParam("searchData", "kommunenr:" + kommunenr)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(TenorResponse.class);
        var response = request.block();

        System.out.println("Fikk respons!");
        return response.getData();
    }
}
