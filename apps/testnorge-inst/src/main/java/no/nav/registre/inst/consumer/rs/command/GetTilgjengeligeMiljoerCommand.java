package no.nav.registre.inst.consumer.rs.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.provider.rs.responses.SupportedEnvironmentsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class GetTilgjengeligeMiljoerCommand implements Callable<List<String>> {

    private final WebClient webClient;
    private final String inst2newServerUrl;

    @SneakyThrows
    @Override
    public List<String> call() {
        try {
            ResponseEntity<SupportedEnvironmentsResponse> response = webClient.get().uri(inst2newServerUrl, uriBuilder ->
                            uriBuilder.path("/v1/environment")
                                    .build())
                    .retrieve().toEntity(SupportedEnvironmentsResponse.class).block();
            List<String> miljoer = nonNull(response) && response.hasBody() && nonNull(response.getBody().getInstitusjonsoppholdEnvironments())
                    ? response.getBody().getInstitusjonsoppholdEnvironments()
                    .stream()
                    .sorted()
                    .collect(Collectors.toList())
                    : emptyList();
            log.info("Tilgjengelige inst2 miljøer: {}", String.join(",", miljoer));
            return miljoer;
        } catch (WebClientResponseException e) {
            log.error("Henting av tilgjengelige miljøer i Inst2 feilet: ", e);
            return new ArrayList<>();
        }
    }
}
