package no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.dto.BlobDTO;

@Slf4j
@RequiredArgsConstructor
public class GetBlobFromShaCommand implements Callable<byte[]> {
    private final WebClient webClient;
    private final String sha;

    @Override
    public byte[] call()  {
        log.info("Henter blob med sha:{}", sha);

        try {
            var blob = webClient
                    .get()
                    .uri(builder -> builder.path("/repos/navikt/testnorge/git/blobs/{sha}").build(sha))
                    .retrieve()
                    .bodyToMono(BlobDTO.class)
                    .block();
            return Base64.getMimeDecoder().decode(blob.getContent());
        } catch (WebClientResponseException e) {
            log.error(
                    "Feil ved henting av blob: {}.",
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}