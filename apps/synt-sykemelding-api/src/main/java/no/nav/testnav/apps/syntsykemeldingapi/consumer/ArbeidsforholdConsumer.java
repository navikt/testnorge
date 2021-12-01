package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.ArbeidsforholdServiceProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.GetArbeidsforholdCommand;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
@Slf4j
public class ArbeidsforholdConsumer {
    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ArbeidsforholdServiceProperties serviceProperties;

    public ArbeidsforholdConsumer(
            TokenExchange tokenExchange,
            ArbeidsforholdServiceProperties serviceProperties
    ) {
        this.tokenExchange = tokenExchange;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @SneakyThrows
    public ArbeidsforholdDTO getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        log.info("Henter arbeidsforhold for {} i org {} med id {}", ident, orgnummer, arbeidsforholdId);
        var token = tokenExchange.exchange(serviceProperties).block().getTokenValue();

        ArbeidsforholdDTO arbeidsforholdDTO = new GetArbeidsforholdCommand(
                webClient,
                token,
                ident,
                orgnummer,
                arbeidsforholdId
        ).call();

        log.info("Arbeidsforhold for ident {} hentet.", arbeidsforholdDTO.getIdent());
        return arbeidsforholdDTO;
    }
}
