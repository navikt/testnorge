package no.nav.registre.sam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sam.consumer.rs.command.PostSaveHistorikkCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.sam.domain.SamSaveInHodejegerenRequest;

@Component
@Slf4j
public class HodejegerenHistorikkConsumer {

    private final WebClient webClient;

    public HodejegerenHistorikkConsumer(@Value("${testnorge-hodejegeren.rest.api.url}") String hodejegerenServerUrl) {
        this.webClient = WebClient.builder().baseUrl(hodejegerenServerUrl).build();
    }

    @Timed(value = "sam.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> saveHistory(
            SamSaveInHodejegerenRequest request
    ) {
        return new PostSaveHistorikkCommand(request, webClient).call();
    }
}
