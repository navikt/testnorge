package no.nav.testnav.apps.instservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.instservice.consumer.command.DeleteInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.command.GetInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.consumer.command.GetTilgjengeligeMiljoerCommand;
import no.nav.testnav.apps.instservice.consumer.command.PostInstitusjonsoppholdCommand;
import no.nav.testnav.apps.instservice.domain.InstitusjonResponse;
import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@Slf4j
public class Inst2Consumer {

    private final WebClient webClient;
    private final String inst2ServerUrl;

    public Inst2Consumer(
            @Value("${consumers.inst2.url}") String inst2ServerUrl,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.inst2ServerUrl = inst2ServerUrl;
        this.webClient = WebClient.builder()
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public InstitusjonResponse hentInstitusjonsoppholdFraInst2(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        return new GetInstitusjonsoppholdCommand(webClient, inst2ServerUrl, miljoe, ident, callId, consumerId).call();
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String callId,
            String consumerId,
            String miljoe,
            InstitusjonsoppholdV2 institusjonsopphold
    ) {
        return new PostInstitusjonsoppholdCommand(webClient, inst2ServerUrl, miljoe, institusjonsopphold, callId, consumerId).call();
    }

    public ResponseEntity<Object> slettInstitusjonsoppholdMedIdent(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        return new DeleteInstitusjonsoppholdCommand(webClient, inst2ServerUrl, miljoe, ident, callId, consumerId).call();
    }

    public List<String> hentInst2TilgjengeligeMiljoer() {
        return new GetTilgjengeligeMiljoerCommand(webClient, inst2ServerUrl).call();
    }
}
