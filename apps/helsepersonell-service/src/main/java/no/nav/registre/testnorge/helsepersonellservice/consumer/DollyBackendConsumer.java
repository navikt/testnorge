package no.nav.registre.testnorge.helsepersonellservice.consumer;

import no.nav.registre.testnorge.helsepersonellservice.config.credentials.DollyBackendProperties;
import no.nav.registre.testnorge.helsepersonellservice.consumer.command.GetDollyGruppeIdenterCommand;
import no.nav.registre.testnorge.helsepersonellservice.domain.RsTestgruppeMedBestillingId;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class DollyBackendConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final DollyBackendProperties serverProperties;
    private final int helsepersonellGruppeId;

    public DollyBackendConsumer(
            TokenExchange tokenExchange,
            DollyBackendProperties serverProperties,
            @Value("${dolly.helsepersonell.gruppeId}") int helsepersonellGruppeId,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.helsepersonellGruppeId = helsepersonellGruppeId;
        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public List<String> getHelsepersonell(){
        return getGruppeIdenter(helsepersonellGruppeId);
    }

    private List<String> getGruppeIdenter(int gruppeId) {
        var response = tokenExchange.exchange(serverProperties).flatMap(accessToken ->
                        new GetDollyGruppeIdenterCommand(gruppeId, webClient, accessToken.getTokenValue()).call())
                .block();

        if (nonNull(response) && !response.getIdenter().isEmpty()) {
            return response.getIdenter().stream()
                    .map(RsTestgruppeMedBestillingId.IdentBestilling::getIdent)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
