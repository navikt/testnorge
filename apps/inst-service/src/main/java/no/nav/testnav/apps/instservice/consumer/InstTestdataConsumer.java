package no.nav.testnav.apps.instservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.instservice.consumer.command.DeleteInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.command.GetInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.command.GetTilgjengeligeMiljoerCommand;
import no.nav.testnav.apps.instservice.consumer.command.PostInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.credential.InstTestdataProperties;
import no.nav.testnav.apps.instservice.domain.InstitusjonResponse;
import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class InstTestdataConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public InstTestdataConsumer(
            TokenExchange tokenExchange,
            InstTestdataProperties instTestdataProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.properties = instTestdataProperties;
        this.webClient = WebClient.builder()
                .baseUrl(instTestdataProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public InstitusjonResponse hentInstitusjonsoppholdFraInst2(
            String miljoe,
            String ident) {

        var response = tokenExchange.exchange(properties)
                .flatMap(accessToken -> new GetInstitusjonsoppholdCommand(
                        webClient, accessToken.getTokenValue(), miljoe, ident).call())
                .block();
        return nonNull(response) ? response : new InstitusjonResponse();
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String miljoe,
            InstitusjonsoppholdV2 institusjonsopphold) {

        return tokenExchange.exchange(properties)
                .flatMap(accessToken -> new PostInstitusjonsoppholdCommand(
                        webClient, accessToken.getTokenValue(), miljoe, institusjonsopphold).call())
                .block();
    }

    public ResponseEntity<Object> slettInstitusjonsoppholdMedIdent(
            String miljoe,
            String ident) {

        var response = tokenExchange.exchange(properties)
                .flatMap(accessToken -> new DeleteInstitusjonsoppholdCommand(
                        webClient, accessToken.getTokenValue(), miljoe, ident).call())
                .block();
        return nonNull(response)
                ? ResponseEntity.status(response.getStatusCode()).body(response.getBody())
                : ResponseEntity.notFound().build();
    }

    public List<String> hentInst2TilgjengeligeMiljoer() {

        var response = tokenExchange.exchange(properties)
                .flatMap(accessToken -> new GetTilgjengeligeMiljoerCommand(
                        webClient, accessToken.getTokenValue()).call())
                .block();
        if (nonNull(response) && !response.getInstitusjonsoppholdEnvironments().isEmpty()) {
            return response.getInstitusjonsoppholdEnvironments().stream().sorted().toList();
        } else {
            return emptyList();
        }
    }
}
