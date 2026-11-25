package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonmottak.config.Consumers;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.StartEregDollyEnheterCommand;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class EregDollyEnheterConsumer {

    private final Consumers consumers;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    @Value("${ereg-dolly.retry.max-nye-forsoek:14}")
    private long maxNyeForsoek;

    @Value("${ereg-dolly.retry.ventetid:30s}")
    private Duration ventetid;

    @Value("${ereg-dolly.svartid:2m}")
    private Duration svartid;

    @Value("${ereg-dolly.tidsavbrudd:8m}")
    private Duration tidsavbrudd;

    @Value("${ereg-dolly.pause-etter-start:15s}")
    private Duration pauseEtterStart;

    public Mono<Void> send(Flatfil flatfil, String miljo) {

        var innhold = flatfil.build().toUpperCase().getBytes(StandardCharsets.ISO_8859_1);

        var serverProperties = consumers.getTestnavDollyProxy();

        return tokenExchange
                .exchange(serverProperties)
                .flatMap(token -> new StartEregDollyEnheterCommand(
                        webClient,
                        serverProperties.getUrl(),
                        token.getTokenValue(),
                        miljo,
                        innhold,
                        maxNyeForsoek,
                        ventetid,
                        svartid
                ).call())
                .timeout(tidsavbrudd)
                .doOnSuccess(_ -> log.info("Flatfil sendt til ereg-dolly-enheter-integrasjon i miljø {}.", miljo))
                .then(Mono.delay(pauseEtterStart))
                .then()
                .onErrorResume(error -> {
                    log.error("Klarte ikke å sende flatfil til ereg-dolly-enheter-integrasjon i miljø {}.", miljo, error);
                    return Mono.empty();
                });
    }
}
