package no.nav.registre.endringsmeldinger.consumer.rs;

import no.nav.registre.endringsmeldinger.consumer.rs.command.PostSendEndringsmeldingTpsfCommand;
import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TpsfConsumer {

    private final String username;
    private final String password;
    private final WebClient webClient;

    public TpsfConsumer(
            @Value("${consumers.tps-forvalteren.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.username = username;
        this.password = password;
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public RsPureXmlMessageResponse sendEndringsmeldingTilTps(SendTilTpsRequest sendTilTpsRequest) {
        return new PostSendEndringsmeldingTpsfCommand(sendTilTpsRequest, username, password, webClient).call();
    }
}
