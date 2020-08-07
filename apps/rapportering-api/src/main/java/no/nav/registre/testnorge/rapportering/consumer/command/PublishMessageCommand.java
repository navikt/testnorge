package no.nav.registre.testnorge.rapportering.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.rapportering.consumer.dto.Message;
import no.nav.registre.testnorge.rapportering.consumer.dto.Response;

@Slf4j
@RequiredArgsConstructor
public class PublishMessageCommand implements Callable<Response> {
    private final String token;
    private final String url;
    private final RestTemplate restTemplate;
    private final Message message;


    @Override
    public Response call() {
        var request = RequestEntity.post(URI.create(url))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
        var response = restTemplate.exchange(request, Response.class);

        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody() || !response.getBody().getOk()) {
            if (response.hasBody() && !response.getBody().getOk()) {
                log.error("Response message from slack {}", response.getBody().getResponse_metadata());
            }
            throw new RuntimeException("Klarer ikke publisere meldinger til slack");

        }
        return response.getBody();
    }
}
