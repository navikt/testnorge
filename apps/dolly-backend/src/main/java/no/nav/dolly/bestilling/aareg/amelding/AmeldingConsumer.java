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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.dolly.bestilling.aareg.command.OrganisasjonGetCommand.NOT_FOUND;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
@Slf4j
public class AmeldingConsumer {

    private static final String JURIDISK_ENHET_IKKE_FUNNET = "Feil= Juridisk enhet for organisasjon(ene): %s ble ikke funnet i miljø";
    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyy-MM");

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
    private final ErrorStatusDecoder errorStatusDecoder;

    public AmeldingConsumer(TokenExchange tokenService,
                            AmeldingServiceProperties serviceProperties,
                            ObjectMapper objectMapper,
                            ErrorStatusDecoder errorStatusDecoder
    ) {

        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
        this.errorStatusDecoder = errorStatusDecoder;
    }

    @Timed(name = "providers", tags = { "operation", "amelding_put" })
    public Flux<String> sendAmeldinger(List<AMeldingDTO> ameldinger, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(ameldinger)
                        .flatMap(amelding -> {
                            if (NOT_FOUND.equals(amelding.getOpplysningspliktigOrganisajonsnummer())) {
                                return Mono.just(ErrorStatusDecoder.encodeStatus(
                                        String.format(JURIDISK_ENHET_IKKE_FUNNET, amelding.getVirksomheter().stream()
                                                .map(VirksomhetDTO::getOrganisajonsnummer)
                                                .collect(Collectors.joining(",")))));
                            } else {
                                log.info("Sender Amelding {} til miljø {}: {}",
                                        amelding.getKalendermaaned().format(YEAR_MONTH), miljoe, amelding);
                                return new AmeldingPutCommand(webClient, amelding, miljoe, token.getTokenValue()).call()
                                        .map(status -> status.getStatusCode().is2xxSuccessful() ? "OK" :
                                                errorStatusDecoder.getErrorText(HttpStatus.valueOf(status.getStatusCode().value()), status.getBody()));
                            }
                        }));
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
