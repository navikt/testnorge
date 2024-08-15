package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.tenor.HentOrganisasjonCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.tenor.HentOrganisasjonerOversiktCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOrganisasjonRequest;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.tenor.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class TenorConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;


    public TenorConsumer(
            TokenExchange tokenExchange,
            Consumers consumers) {

        this.serverProperties = consumers.getTestnavTenorSearchService();
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public TenorOversiktOrganisasjonResponse hentOrganisasjonerOversikt(TenorOrganisasjonRequest tenorOrgRequest, String antallOrganisasjoner) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new HentOrganisasjonerOversiktCommand(webClient, token.getTokenValue(),
                        tenorOrgRequest, antallOrganisasjoner).call())
                .block();
    }

    public TenorOversiktOrganisasjonResponse hentOrganisasjon(TenorOrganisasjonRequest tenorOrgRequest) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new HentOrganisasjonCommand(webClient, token.getTokenValue(), tenorOrgRequest).call())
                .block();
    }
}
