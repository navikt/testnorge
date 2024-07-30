package no.nav.levendearbeidsforholdscheduler.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.levendearbeidsforholdscheduler.config.Consumers;
import no.nav.levendearbeidsforholdscheduler.consumer.command.AnsettelseCommand;
import no.nav.levendearbeidsforholdscheduler.consumer.command.AnsettelsesCommand2;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class AnsettelseConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public  AnsettelseConsumer(Consumers consumers,
                               TokenExchange tokenExchange){
        this.serverProperties = consumers.getTestnavLevendeArbeidsforholdAnsettelsev2();
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        log.info("Webclient {}", serverProperties.getUrl());
    }
    public void hentFraAnsettelse(){
        try {
            var token = tokenExchange.exchange(serverProperties).block();
            log.info("Hentet token: {}", token.getTokenValue());
            String response = new AnsettelsesCommand2(webClient, token.getTokenValue()).call();
            log.info("Response {}", response);
        } catch (Exception e) {
            log.error("Feil ved hentet token: {}", e.getMessage());
        }
    }


}
