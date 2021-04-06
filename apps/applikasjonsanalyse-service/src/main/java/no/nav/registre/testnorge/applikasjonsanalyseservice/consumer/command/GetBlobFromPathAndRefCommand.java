package no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.dto.BlobDTO;

@Slf4j
@RequiredArgsConstructor
public class GetBlobFromPathAndRefCommand implements Callable<byte[]> {
    private final WebClient webClient;
    private final String path;
    private final String ref;

    @Override
    public byte[] call()  {
        log.info("Henter blob med path:{}", path);
        try {
            var blob = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/repos/navikt/testnorge/contents/{path}")
                            .queryParam("ref", ref)
                            .build(path)
                    )
                    .retrieve()
                    .bodyToMono(BlobDTO.class)
                    .block();
            return Base64.getMimeDecoder().decode(blob.getContent());
        } catch (WebClientResponseException e) {
            log.error(
                    "Feil ved henting av blob fra path: {}.",
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}