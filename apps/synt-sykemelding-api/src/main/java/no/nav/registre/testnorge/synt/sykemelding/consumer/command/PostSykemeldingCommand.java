package no.nav.registre.testnorge.synt.sykemelding.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.synt.sykemelding.domain.Sykemelding;

@RequiredArgsConstructor
public class PostSykemeldingCommand implements Callable<ResponseEntity<String>> {
    private final RestTemplate restTemplate;
    private final String url;
    private final Sykemelding sykemelding;

    @SneakyThrows
    @Override
    public ResponseEntity<String> call() {
        ResponseEntity<String> response = restTemplate.exchange(
                RequestEntity.post(new URI(url + "/api/v1/sykemeldinger")).body(sykemelding.toDTO()),
                String.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Klarer ikke å opprette sykemelding for " + sykemelding.getIdent());
        }
        return response;
    }
}
