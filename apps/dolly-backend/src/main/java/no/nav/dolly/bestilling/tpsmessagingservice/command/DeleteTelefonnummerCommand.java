package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class DeleteTelefonnummerCommand implements Callable<List<TpsMeldingResponseDTO>> {

    private static final String MILJOER_PARAM = "miljoer";
    private static final String TELEFONTYPER_PARAM = "telefontyper";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final List<String> telefontyper;
    private final String urlPath;
    private final String token;

    @Override
    public List<TpsMeldingResponseDTO> call() {

        log.trace("Sender delete request på ident: {} til TPS messaging service", ident);

        var response = webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(urlPath)
                        .queryParam(MILJOER_PARAM, miljoer)
                        .queryParam(TELEFONTYPER_PARAM, telefontyper)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(TpsMeldingResponseDTO[].class)
                .block();

        log.trace("Response fra TPS messaging service: {}", response);
        return Arrays.asList(response);
    }
}
