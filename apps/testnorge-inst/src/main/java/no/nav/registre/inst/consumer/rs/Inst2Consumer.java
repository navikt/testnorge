package no.nav.registre.inst.consumer.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.domain.InstitusjonResponse;
import no.nav.registre.inst.domain.Institusjonsopphold;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.registre.inst.exception.UgyldigIdentResponseException;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.provider.rs.responses.SupportedEnvironmentsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.registre.inst.properties.HttpRequestConstants.ACCEPT;
import static no.nav.registre.inst.properties.HttpRequestConstants.AUTHORIZATION;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

@Component
@Slf4j
public class Inst2Consumer {

    private static final ParameterizedTypeReference<InstitusjonResponse> RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_OBJECT = new ParameterizedTypeReference<>() {
    };
    private static final String INSTITUSJONSOPPHOLD_PERSON = "/v1/institusjonsopphold/person";
    private static final String ENVIRONMENTS = "environments";
    private static final String NORSKIDENT = "norskident";

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

    public List<Institusjonsopphold> hentInstitusjonsoppholdFraInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        InstitusjonResponse response;
        try {
            ResponseEntity<InstitusjonResponse> listResponseEntity = webClient.get()
                    .uri(inst2NewServerUrl,
                            uriBuilder -> uriBuilder
                                    .path(INSTITUSJONSOPPHOLD_PERSON)
                                    .queryParam(ENVIRONMENTS, miljoe)
                                    .build())
                    .header(ACCEPT, "*/*")
                    .header(AUTHORIZATION, bearerToken)
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .header(NORSKIDENT, ident)
                    .retrieve()
                    .toEntity(RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD)
                    .block();
            response = nonNull(listResponseEntity)
                    ? listResponseEntity.getBody()
                    : new InstitusjonResponse();
            log.info("Hentet Inst2 opphold: " + response);
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke hente ident fra inst2", e);
            throw new UgyldigIdentResponseException("Kunne ikke hente ident fra inst2", e);
        }
        return response.getQ2(); //TODO: FIX!
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            InstitusjonsoppholdV2 institusjonsopphold
    ) {
        try {
            var response = webClient.post()
                    .uri(inst2NewServerUrl, uriBuilder -> uriBuilder
                            .path(INSTITUSJONSOPPHOLD_PERSON)
                            .queryParam(ENVIRONMENTS, miljoe)
                            .build())
                    .header(ACCEPT, "*/*")
                    .header(AUTHORIZATION, bearerToken)
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .bodyValue(institusjonsopphold).retrieve().toEntity(RESPONSE_TYPE_OBJECT)
                    .block();

            Institusjonsopphold institusjonsoppholdResponse = new ObjectMapper().convertValue(response.getBody(), Institusjonsopphold.class);
            return OppholdResponse.builder()
                    .status(response.getStatusCode())
                    .institusjonsopphold(institusjonsoppholdResponse)
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke legge til institusjonsopphold i inst2 på ident - {}", e.getResponseBodyAsString(), e);
            return OppholdResponse.builder()
                    .status(e.getStatusCode())
                    .feilmelding(e.getResponseBodyAsString())
                    .build();
        }
    }

    public ResponseEntity<Object> slettInstitusjonsoppholdMedIdent(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        try {
            var response = webClient.delete()
                    .uri(inst2NewServerUrl, uriBuilder -> uriBuilder
                            .path(INSTITUSJONSOPPHOLD_PERSON)
                            .queryParam(ENVIRONMENTS, miljoe)
                            .build())
                    .header(ACCEPT, "*/*")
                    .header(AUTHORIZATION, bearerToken)
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .header(NORSKIDENT, ident)
                    .retrieve()
                    .toEntity(RESPONSE_TYPE_OBJECT)
                    .block();

            return nonNull(response)
                    ? ResponseEntity.status(response.getStatusCode()).body(response.getBody())
                    : ResponseEntity.notFound().build();
        } catch (WebClientResponseException e) {
            log.error("Kunne ikke slette institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public HttpStatus finnesInstitusjonPaaDato(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            String tssEksternId,
            LocalDate date
    ) {
        try {
            var response = webClient.get().uri(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/institusjon/oppslag/tssEksternId/{tssEksternId}?date={date}")
                            .expand(tssEksternId, date))
                    .header(ACCEPT, "*/*")
                    .header(AUTHORIZATION, bearerToken)
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return nonNull(response) ? response.getStatusCode() : HttpStatus.NOT_FOUND;
        } catch (WebClientResponseException e) {
            log.debug("Institusjon med tssEksternId {} er ikke gyldig på dato {}.", tssEksternId, date);
            return e.getStatusCode();
        }
    }

    public List<String> hentInst2TilgjengeligeMiljoer() {
        try {
            ResponseEntity<SupportedEnvironmentsResponse> response = webClient.get().uri(inst2NewServerUrl, uriBuilder ->
                            uriBuilder.path("/v1/environment")
                                    .build())
                    .retrieve().toEntity(SupportedEnvironmentsResponse.class).block();
            List<String> miljoer = nonNull(response) && response.hasBody() ? response.getBody().getInstitusjonsoppholdEnvironments() : emptyList();
            log.info("Tilgjengelige inst2 miljøer: {}", String.join(",", miljoer));
            return miljoer;
        } catch (WebClientResponseException e) {
            log.error("Henting av tilgjengelige miljøer i Inst2 feilet: ", e);
            return new ArrayList<>();
        }
    }
}
