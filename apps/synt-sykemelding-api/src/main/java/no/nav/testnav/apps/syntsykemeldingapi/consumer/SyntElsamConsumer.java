package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.Consumers;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSyntSykemeldingCommand;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class SyntElsamConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public SyntElsamConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getSyntSykemelding();
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @SneakyThrows
    public SyntSykemeldingHistorikkDTO genererSykemeldinger(String ident, LocalDate startDato) {
        log.info("Genererer sykemelding for {} fom {}", ident, startDato.toString());

        var request = Map.of(ident, startDato.toString());

        var response = tokenExchange.exchange(serverProperties).flatMap(accessToken ->
                        new PostSyntSykemeldingCommand(request, accessToken.getTokenValue(), webClient).call())
                .block();

        if (isNull(response)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Klarte ikke å generere sykemeldinger.");
        }
        log.info("Sykemelding generert.");
        return response.get(ident);
    }
}