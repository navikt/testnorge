package no.nav.registre.testnorge.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.StatusPaaIdenterDTO;

@Slf4j
@RequiredArgsConstructor
public class GetIdentEnvironmentsCommand implements Callable<Set<String>> {
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Set<String> call() {
        var dto = webClient
                .get()
                .uri(builder -> builder.path("/api/v1/testdata/tpsStatus")
                        .queryParam("identer", ident)
                        .queryParam("includeProd", false).build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(StatusPaaIdenterDTO.class)
                .block();

        if (dto.getStatusPaaIdenter().isEmpty() || dto.getStatusPaaIdenter().get(0).getEnv().isEmpty()) {
            return null;
        }

        return dto.getStatusPaaIdenter().get(0).getEnv();
    }
}
