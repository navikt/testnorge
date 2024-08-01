package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.ereg.HentOrganisasjonerCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.organisasjon.Organisasjon;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
//import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;

@Slf4j
@Component
public class EregConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;


    public EregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getTestnavEregProxy();
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Organisasjon getOrganisasjon(String orgnummer, String miljo) {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        OrganisasjonDTO dto = new HentOrganisasjonerCommand(webClient, accessToken.getTokenValue(), miljo, orgnummer).call();
        return dto != null ? new Organisasjon(dto) : null;
    }

}
