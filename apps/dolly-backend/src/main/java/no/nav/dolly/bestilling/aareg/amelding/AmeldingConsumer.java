package no.nav.dolly.bestilling.aareg.amelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.command.AmeldingPutCommand;
import no.nav.dolly.config.credentials.AmeldingServiceProperties;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.VirksomhetDTO;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static no.nav.dolly.bestilling.aareg.command.OrganisasjonGetCommand.NOT_FOUND;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
@Slf4j
public class AmeldingConsumer {

    private static final String JURIDISK_ENHET_IKKE_FUNNET = "Juridisk enhet for organisasjon: %s ikke funnet i miljø: %s";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
    private final ErrorStatusDecoder errorStatusDecoder;

    public AmeldingConsumer(TokenExchange tokenService,
                            AmeldingServiceProperties serviceProperties,
                            ObjectMapper objectMapper,
                            ErrorStatusDecoder errorStatusDecoder,
                            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
        this.errorStatusDecoder = errorStatusDecoder;
    }

    @Timed(name = "providers", tags = { "operation", "amelding_put" })
    public Flux<String> sendAmeldinger(List<AMeldingDTO> ameldinger, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(ameldinger)
                        .flatMap(amelding -> {
                            if (NOT_FOUND.equals(amelding.getOpplysningspliktigOrganisajonsnummer())) {
                                return Mono.just(String.format(JURIDISK_ENHET_IKKE_FUNNET, amelding.getVirksomheter().stream()
                                        .map(VirksomhetDTO::getOrganisajonsnummer)
                                        .findFirst().orElse("Ukjent"), miljoe));
                        } else {
                                return new AmeldingPutCommand(webClient, amelding, miljoe, token.getTokenValue()).call()
                                        .map(status -> status.getStatusCode().is2xxSuccessful() ? "OK" :
                                                errorStatusDecoder.getErrorText(status.getStatusCode(), status.getBody()));
                            }
                        }));
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
