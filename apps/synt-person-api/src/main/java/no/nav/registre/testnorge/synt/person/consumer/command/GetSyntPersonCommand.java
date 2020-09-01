package no.nav.registre.testnorge.synt.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.synt.person.consumer.dto.SyntPersonDTO;

@Slf4j
@RequiredArgsConstructor
public class GetSyntPersonCommand implements Callable<SyntPersonDTO> {
    private final WebClient webClient;
    private static final String INNVANDRING = "0211";

    @Override
    public SyntPersonDTO call() {
        log.info("Genererer ny syntetisk person fra syntrest...");
        SyntPersonDTO[] response = webClient.get().uri(builder -> builder
                .path("/v1/generate/tps/{type}")
                .queryParam("numToGenerate", "1")
                .build(INNVANDRING)
        ).retrieve().bodyToMono(SyntPersonDTO[].class).block();

        if (response == null || response.length == 0) {
            throw new RuntimeException("Klarte ikke å opprette person fra syntrest");
        }
        log.info("Syntetisk person generert.");
        return response[0];
    }
}
