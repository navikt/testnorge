package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.tenor.HentOrganisasjonCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.tenor.HentOrganisasjonerOversiktCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TenorConsumer {

    private static final String TENOR_DOMAIN = "https://testnav-tenor-search-service.intern.dev.nav.no";
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;


    public TenorConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            ObjectMapper objectMapper) {

        this.serverProperties = consumers.getTestnavTenorSearchService();
        this.tokenExchange = tokenExchange;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies
                .builder()
                .codecs(
                        config -> {
                            config
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            config
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                .build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public TenorOversiktOrganisasjonResponse hentOrganisasjonerOversikt(TenorOrganisasjonRequest tenorOrgRequest, String antallOrganisasjoner) {
        var accessToken = tokenExchange.exchange(serverProperties).block();

        if (nonNull(accessToken)) {
            var token = accessToken.getTokenValue();
            return new HentOrganisasjonerOversiktCommand(webClient, token, tenorOrgRequest, antallOrganisasjoner).call();
        }
        return new TenorOversiktOrganisasjonResponse();
    }

    public TenorOversiktOrganisasjonResponse hentOrganisasjon(TenorOrganisasjonRequest tenorOrgRequest) {
        var accessToken = tokenExchange.exchange(serverProperties).block();

        if (nonNull(accessToken)) {
            var token = accessToken.getTokenValue();
            return new HentOrganisasjonCommand(webClient, token, tenorOrgRequest).call();
        }
        return new TenorOversiktOrganisasjonResponse();
    }
}
