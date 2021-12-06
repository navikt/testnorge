package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SendEgenansattCommand implements Callable<List<TpsMeldingResponseDTO>> {

    private static final String MILJOER_PARAM = "miljoer";
    private static final String EGENANSATT_FRA_PARAM = "fraOgMed";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final LocalDate datoFra;
    private final String urlPath;
    private final String token;

    @Override
    public List<TpsMeldingResponseDTO> call() {

        log.trace("Sender request på ident: {} til TPS messaging service: {}", ident, datoFra);

        var response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlPath)
                        .queryParam(MILJOER_PARAM, miljoer)
                        .queryParam(EGENANSATT_FRA_PARAM, datoFra)
                        .build(ident))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(TpsMeldingResponseDTO[].class)
                .block();

        log.trace("Response fra TPS messaging service: {}", response);
        return Arrays.asList(response);
    }
}
