package no.nav.registre.skd.consumer.command.tpsf;

import java.util.concurrent.Callable;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteIdenterFraTpsCommand implements Callable<Void> {

    private final String miljoerSomString;
    private final String identerSomString;
    private final WebClient webClient;

    @Override
    public Void call() {
        return webClient.delete()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/deleteFromTps")
                                .queryParam("miljoer", miljoerSomString)
                                .queryParam("identer", identerSomString)
                                .build()
                )
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
