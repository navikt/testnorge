package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.apps.syntsykemeldingapi.exception.GenererSykemeldingerException;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostSykemeldingCommand implements Callable<ResponseEntity<String>> {
    private final RestTemplate restTemplate;
    private final String url;
    private final Sykemelding sykemelding;

    @SneakyThrows
    @Override
    public ResponseEntity<String> call() {

        log.info("Sender sykemelding til sykemelding-api: {}", Json.pretty(sykemelding));
        ResponseEntity<String> response = restTemplate.exchange(
                RequestEntity.post(new URI(url + "/api/v1/sykemeldinger")).body(sykemelding.toDTO()),
                String.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new GenererSykemeldingerException("Klarer ikke å opprette sykemelding for " + sykemelding.getIdent());
        }
        return response;
    }
}
