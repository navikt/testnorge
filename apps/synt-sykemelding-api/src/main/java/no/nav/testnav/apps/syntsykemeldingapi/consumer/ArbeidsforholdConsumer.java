package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.ArbeidsforholdServiceProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.GetArbeidsforholdCommand;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class ArbeidsforholdConsumer {
    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ArbeidsforholdServiceProperties serviceProperties;

    public ArbeidsforholdConsumer(
            TokenExchange tokenExchange,
            ArbeidsforholdServiceProperties serviceProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @SneakyThrows
    public ArbeidsforholdDTO getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        log.info("Henter arbeidsforhold for {} i org {} med id {}", ident, orgnummer, arbeidsforholdId);
        var response = tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetArbeidsforholdCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        ident,
                        orgnummer,
                        arbeidsforholdId
                ).call()).block();

        log.info("Arbeidsforhold for ident {} hentet.", response.getIdent());
        return response;
    }
}
