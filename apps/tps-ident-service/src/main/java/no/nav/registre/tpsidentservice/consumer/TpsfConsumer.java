package no.nav.registre.tpsidentservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.registre.tpsidentservice.consumer.command.GetTpsStatusCommand;
import no.nav.registre.tpsidentservice.consumer.dto.TpsStatus;

@Slf4j
@Component
public class TpsfConsumer {
    private final WebClient webClient;

    public TpsfConsumer(@Value("${consumers.tpsf.url}") String url) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public String getIdent(String ident) {
        TpsStatus status = new GetTpsStatusCommand(webClient, ident).call();

        if (status.getMiljoer().isEmpty()) {
            return null;
        }
        return status.getIdent();
    }

    public Set<String> getMiljoer(String ident) {
        TpsStatus status = new GetTpsStatusCommand(webClient, ident).call();
        return status.getMiljoer();
    }

}
