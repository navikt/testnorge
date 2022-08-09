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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Component
@Slf4j
public class InstTestdataConsumer {

    private final WebClient webClient;

    public InstTestdataConsumer(
            InstTestdataProperties instTestdataProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

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
        return new GetInstitusjonsoppholdCommand(webClient, miljoe, ident, callId, consumerId).call();
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String callId,
            String consumerId,
            String miljoe,
            InstitusjonsoppholdV2 institusjonsopphold
    ) {
        return new PostInstitusjonsoppholdCommand(webClient, miljoe, institusjonsopphold, callId, consumerId).call();
    }

    public ResponseEntity<Object> slettInstitusjonsoppholdMedIdent(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        return new DeleteInstitusjonsoppholdCommand(webClient, miljoe, ident, callId, consumerId).call();
    }

    public List<String> hentInst2TilgjengeligeMiljoer() {
        var response = new GetTilgjengeligeMiljoerCommand(webClient).call();

        if (nonNull(response) && !response.getInstitusjonsoppholdEnvironments().isEmpty()){
            return response.getInstitusjonsoppholdEnvironments().stream().sorted().toList();
        } else{
            return emptyList();
        }
    }
}
