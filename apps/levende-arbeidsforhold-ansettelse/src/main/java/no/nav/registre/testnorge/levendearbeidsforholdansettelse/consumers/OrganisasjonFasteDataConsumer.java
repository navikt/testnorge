package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.organisasjonFasteData.GetOrganisasjonerCommand;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class OrganisasjonFasteDataConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public OrganisasjonFasteDataConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getTestnavOrganisasjonFasteDataService();
        log.info("ServerProperties: {}", serverProperties);
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public List<OrganisasjonDTO> hentOrganisasjoner() {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        return new GetOrganisasjonerCommand(webClient, accessToken.getTokenValue()).call(); //org -> new Organisasjon(org.getOrganisasjonsnummer())
    }
}
