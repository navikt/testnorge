package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.tenor.HentOrganisasjonerCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
//import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

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

    public List<OrganisasjonDTO> hentOrganisasjoner(TenorOrganisasjonRequest tenorOrgRequest, String antallOrganisasjoner) throws JsonProcessingException {
        System.out.println("I hentOrganisasjoner");
        var accessToken = tokenExchange.exchange(serverProperties).block();
        System.out.println("Har hentet ut token");

        if (nonNull(accessToken)) {
            var token = accessToken.getTokenValue();
            return new HentOrganisasjonerCommand(webClient, token, tenorOrgRequest, antallOrganisasjoner).call();
        }
        return new ArrayList<>();
    }
}
