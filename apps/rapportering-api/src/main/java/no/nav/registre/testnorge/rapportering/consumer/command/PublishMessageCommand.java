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

@Slf4j
@RequiredArgsConstructor
public class PublishMessageCommand implements Callable<Void> {
    private final String token;
    private final String url;
    private final RestTemplate restTemplate;
    private final Message message;


    @Override
    public Void call() {
        var request = RequestEntity.post(URI.create(url))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
        var response = restTemplate.exchange(request, Void.class);

        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Klarer ikke publisere meldinger til slack");
        }
        return response.getBody();
    }
}
