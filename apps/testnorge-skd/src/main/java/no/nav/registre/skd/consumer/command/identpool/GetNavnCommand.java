package no.nav.registre.skd.consumer.command.identpool;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.response.Navn;

@Slf4j
public class GetNavnCommand implements Callable<List<Navn>> {

    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<Navn>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public GetNavnCommand(WebClient webClient){
        this.webClient = webClient;
    }

    @Override
    public List<Navn> call(){
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/v1/fiktive-navn/tilfeldig")
                                    .queryParam("antall", 1)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å hente navn fra Ident-pool.", e);
            return null;
        }
    }
}
