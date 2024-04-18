package no.nav.dolly.bestilling.aareg.amelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.command.AmeldingPutCommand;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.VirksomhetDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private final ServerProperties serverProperties;
    private final ErrorStatusDecoder errorStatusDecoder;

    public AmeldingConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            ErrorStatusDecoder errorStatusDecoder,
            WebClient.Builder webClientBuilder) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavAmeldingService();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
        this.errorStatusDecoder = errorStatusDecoder;
    }

    @Timed(name = "providers", tags = {"operation", "amelding_put"})
    public Flux<String> sendAmeldinger(List<AMeldingDTO> ameldinger, String miljoe) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.fromIterable(ameldinger)
                        .flatMap(amelding -> {
                            if (NOT_FOUND.equals(amelding.getOpplysningspliktigOrganisajonsnummer())) {
                                return Mono.just(ErrorStatusDecoder.encodeStatus(
                                        String.format(JURIDISK_ENHET_IKKE_FUNNET, amelding.getVirksomheter().stream()
                                                .map(VirksomhetDTO::getOrganisajonsnummer)
                                                .collect(Collectors.joining(",")))));
                            } else {
                                log.info("Sender Amelding {} til miljø {}: {}",
                                        amelding.getKalendermaaned().format(YEAR_MONTH), miljoe, Json.pretty(amelding));
                                return new AmeldingPutCommand(webClient, amelding, miljoe, token.getTokenValue()).call()
                                        .map(status -> status.getStatusCode().is2xxSuccessful() ? "OK" :
                                                errorStatusDecoder.getErrorText(HttpStatus.valueOf(status.getStatusCode().value()), status.getBody()))
                                        .doOnNext(status ->
                                                log.atLevel("OK".equals(status) ? Level.INFO : Level.ERROR)
                                                        .log("Ameldingstatus: {}, miljoe: {}, kalendermåned: {}, organisasjon: {}",
                                                                status, miljoe, amelding.getKalendermaaned(),
                                                                amelding.getOpplysningspliktigOrganisajonsnummer()));
                            }
                        }));
    }
}
