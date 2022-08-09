package no.nav.testnav.apps.instservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.instservice.consumer.command.DeleteInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.command.GetInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.command.GetTilgjengeligeMiljoerCommand;
import no.nav.testnav.apps.instservice.consumer.command.PostInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.credential.InstTestdataProperties;
import no.nav.testnav.apps.instservice.domain.InstitusjonResponse;
import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.exception.UgyldigIdentResponseException;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

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
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        try {
            var response = tokenExchange.exchange(properties)
                    .flatMap(accessToken -> new GetInstitusjonsoppholdCommand(
                            webClient, accessToken.getTokenValue(), miljoe, ident, callId, consumerId).call())
                    .block();
            return nonNull(response) ? response : new InstitusjonResponse();
        } catch (Exception e) {
            log.error("Kunne ikke hente ident fra inst2", e);
            throw new UgyldigIdentResponseException("Kunne ikke hente ident fra inst2", e);
        }
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String callId,
            String consumerId,
            String miljoe,
            InstitusjonsoppholdV2 institusjonsopphold
    ) {
        var token = Objects.requireNonNull(tokenExchange.exchange(properties).block()).getTokenValue();
        return new PostInstitusjonsoppholdCommand(webClient, token, miljoe, institusjonsopphold, callId, consumerId).call();
    }

    public ResponseEntity<Object> slettInstitusjonsoppholdMedIdent(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        try {
            var response = tokenExchange.exchange(properties)
                    .flatMap(accessToken -> new DeleteInstitusjonsoppholdCommand(
                            webClient, accessToken.getTokenValue(), miljoe, ident, callId, consumerId).call())
                    .block();
            return nonNull(response)
                    ? ResponseEntity.status(response.getStatusCode()).body(response.getBody())
                    : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Kunne ikke slette institusjonsopphold: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public List<String> hentInst2TilgjengeligeMiljoer() {
        try {
            var response = tokenExchange.exchange(properties)
                    .flatMap(accessToken -> new GetTilgjengeligeMiljoerCommand(webClient, accessToken.getTokenValue()).call())
                    .block();
            if (nonNull(response) && !response.getInstitusjonsoppholdEnvironments().isEmpty()) {
                return response.getInstitusjonsoppholdEnvironments().stream().sorted().toList();
            } else {
                return emptyList();
            }
        } catch (Exception e) {
            log.error("Henting av tilgjengelige miljøer i Inst2 feilet: ", e);
            return emptyList();
        }
    }
}
