package no.nav.registre.inst.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.consumer.rs.command.DeleteInstitusjonsoppholdCommand;
import no.nav.registre.inst.consumer.rs.command.ExistsInstitusjonsoppholdCommand;
import no.nav.registre.inst.consumer.rs.command.GetInstitusjonsoppholdCommand;
import no.nav.registre.inst.consumer.rs.command.PostInstitusjonsoppholdCommand;
import no.nav.registre.inst.domain.InstitusjonResponse;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.provider.rs.responses.SupportedEnvironmentsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Component
@Slf4j
public class Inst2Consumer {

    private final WebClient webClient;
    private final UriTemplate inst2WebApiServerUrl;
    private final String inst2NewServerUrl;

    public Inst2Consumer(
            @Value("${inst2.web.api.url}") String inst2WebApiServerUrl,
            @Value("${inst2.new.api.url}") String inst2NewServerUrl
    ) {
        this.inst2WebApiServerUrl = new UriTemplate(inst2WebApiServerUrl);
        this.inst2NewServerUrl = inst2NewServerUrl;
        this.webClient = WebClient
                .builder()
                .build();
    }

    public InstitusjonResponse hentInstitusjonsoppholdFraInst2(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        return new GetInstitusjonsoppholdCommand(webClient, inst2NewServerUrl, miljoe, ident, callId, consumerId).call();
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String callId,
            String consumerId,
            String miljoe,
            InstitusjonsoppholdV2 institusjonsopphold
    ) {
        return new PostInstitusjonsoppholdCommand(webClient, inst2NewServerUrl, miljoe, institusjonsopphold, callId, consumerId).call();
    }

    public ResponseEntity<Object> slettInstitusjonsoppholdMedIdent(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        return new DeleteInstitusjonsoppholdCommand(webClient, inst2NewServerUrl, miljoe, ident, callId, consumerId).call();
    }

    public HttpStatus finnesInstitusjonPaaDato(
            String callId,
            String consumerId,
            String miljoe,
            String tssEksternId,
            LocalDate date
    ) {
        return new ExistsInstitusjonsoppholdCommand(webClient, inst2WebApiServerUrl, miljoe, date, tssEksternId, callId, consumerId).call();
    }

    public List<String> hentInst2TilgjengeligeMiljoer() {
        try {
            ResponseEntity<SupportedEnvironmentsResponse> response = webClient.get().uri(inst2NewServerUrl, uriBuilder ->
                            uriBuilder.path("/v1/environment")
                                    .build())
                    .retrieve().toEntity(SupportedEnvironmentsResponse.class).block();
            List<String> miljoer = nonNull(response) && response.hasBody() && nonNull(response.getBody().getInstitusjonsoppholdEnvironments())
                    ? response.getBody().getInstitusjonsoppholdEnvironments()
                    .stream()
                    .sorted()
                    .collect(Collectors.toList())
                    : emptyList();
            log.info("Tilgjengelige inst2 miljøer: {}", String.join(",", miljoer));
            return miljoer;
        } catch (WebClientResponseException e) {
            log.error("Henting av tilgjengelige miljøer i Inst2 feilet: ", e);
            return new ArrayList<>();
        }
    }
}
