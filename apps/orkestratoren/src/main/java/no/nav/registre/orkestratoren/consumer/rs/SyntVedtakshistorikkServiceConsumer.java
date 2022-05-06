package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.command.OpprettArbeissoekerCommand;
import no.nav.registre.orkestratoren.consumer.rs.command.OpprettVedtakshistorikkCommand;
import no.nav.registre.orkestratoren.consumer.rs.credentials.SyntVedtakshistorikkServiceProperties;
import no.nav.registre.orkestratoren.exception.ArenaSyntetiseringException;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static java.util.Objects.nonNull;

@Component
@Slf4j
public class SyntVedtakshistorikkServiceConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntVedtakshistorikkServiceConsumer(
            SyntVedtakshistorikkServiceProperties syntProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = syntProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntProperties.getUrl())
                .build();
    }
    @Timed(value = "orkestratoren.resource.latency", extraTags = {"operation", "arena"})
    public Map<String, NyeBrukereResponse> opprettArbeidsoekereMedOppfoelging(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        try{
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new OpprettArbeissoekerCommand(syntetiserArenaRequest, accessToken.getTokenValue(), webClient).call())
                    .block();
        }catch (Exception e) {
            throw new ArenaSyntetiseringException("Feil under opprettelse av bruker i Arena.");
        }
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettVedtakshistorikk(
            SyntetiserArenaRequest request
    ) {
        if (nonNull(request)){
            try {
                var response = tokenExchange.exchange(serviceProperties)
                        .flatMap(accessToken -> new OpprettVedtakshistorikkCommand(request, accessToken.getTokenValue(), webClient).call())
                        .block();

                List<NyttVedtakResponse> alleOpprettedeRettigheter = new ArrayList<>();
                if (nonNull(response)) {
                    response.values().forEach(alleOpprettedeRettigheter::addAll);
                }
                return alleOpprettedeRettigheter;
            } catch (Exception e) {
                log.error("Feil under syntetisering av vedtakshistorikk", e);
            }
        }
        return Collections.emptyList();
    }

}
