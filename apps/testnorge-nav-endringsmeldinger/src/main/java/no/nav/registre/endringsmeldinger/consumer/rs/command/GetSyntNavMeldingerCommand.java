package no.nav.registre.endringsmeldinger.consumer.rs.command;

import lombok.AllArgsConstructor;
import no.nav.registre.endringsmeldinger.consumer.rs.exceptions.SyntetiseringsException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;

import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class GetSyntNavMeldingerCommand implements Callable<List<Document>> {

    private final String endringskode;
    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<Document>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<Document> call() {
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/generate/nav/{endringskode}/{antallMeldinger}")
                                    .build(endringskode, antallMeldinger)
                    )
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            throw new SyntetiseringsException(e.getMessage(), e.getCause());
        }
    }
}
