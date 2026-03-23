package no.nav.registre.testnorge.sykemelding.consumer;

import no.nav.registre.testnorge.sykemelding.config.Consumers;
import no.nav.registre.testnorge.sykemelding.consumer.command.SyfosmreglerPostValidateCommand;
import no.nav.registre.testnorge.sykemelding.dto.ReceivedSykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.ValidationResultDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SyfosmreglerConsumer {

    private final ServerProperties properties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    public SyfosmreglerConsumer(
            Consumers consumers,
            WebClient webClient,
            TokenExchange tokenExchange
    ) {
        this.properties = consumers.getSykemeldingProxy();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Mono<ValidationResultDTO> validate(ReceivedSykemeldingDTO sykemelding) {

        return tokenExchange.exchange(properties)
                .flatMap(token -> new SyfosmreglerPostValidateCommand(webClient, sykemelding, token.getTokenValue()).call());
    }
}