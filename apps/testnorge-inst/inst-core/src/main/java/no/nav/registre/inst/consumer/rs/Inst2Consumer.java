package no.nav.registre.inst.consumer.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.exception.UgyldigIdentResponseException;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient;
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

    private static final ParameterizedTypeReference<List<Institusjonsopphold>> RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_OBJECT = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;

    private UriTemplate inst2WebApiServerUrl;
    private String inst2NewServerUrl;

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
        List<Institusjonsopphold> response;
        try {
            ResponseEntity<List<Institusjonsopphold>> listResponseEntity = webClient.get()
                    .uri(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/person/institusjonsopphold").expand())
                    .header(ACCEPT, "*/*")
                    .header(AUTHORIZATION, bearerToken)
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .header("Nav-Personident", ident)
                    .retrieve()
                    .toEntity(RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD)
                    .block();
            response = nonNull(listResponseEntity)
                    ? listResponseEntity.getBody()
                    : emptyList();
        } catch (HttpStatusCodeException e) {
            assert e.getMessage() != null;
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ArrayList<>();
            } else {
                log.error("Kunne ikke hente ident fra inst2", e);
                throw new UgyldigIdentResponseException("Kunne ikke hente ident fra inst2", e);
            }
        }
        return response;
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            Institusjonsopphold institusjonsopphold
    ) {
        try {
            var response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(inst2NewServerUrl + "/v1/institusjonsopphold/person")
                            .queryParam("environments", miljoe)
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
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge til institusjonsopphold i inst2 på ident - {}", e.getResponseBodyAsString(), e);
            return OppholdResponse.builder()
                    .status(e.getStatusCode())
                    .feilmelding(e.getResponseBodyAsString())
                    .build();
        }
    }

    public ResponseEntity<Object> oppdaterInstitusjonsoppholdIInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            Long oppholdId,
            Institusjonsopphold institusjonsopphold
    ) {
        try {
            var response = webClient.put().uri(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/person/institusjonsopphold/{oppholdId}").expand(oppholdId))
                    .header(ACCEPT, "*/*")
                    .header(AUTHORIZATION, bearerToken)
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .bodyValue(institusjonsopphold)
                    .retrieve()
                    .toEntity(RESPONSE_TYPE_OBJECT)
                    .block();

            return nonNull(response)
                    ? ResponseEntity.status(response.getStatusCode()).body(response.getBody())
                    : ResponseEntity.notFound().build();
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke oppdatere institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
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
                    .uri(uriBuilder -> uriBuilder
                            .path(inst2NewServerUrl + "/v1/institusjonsopphold/person")
                            .queryParam("environments", miljoe)
                            .queryParam("norskIdent", ident)
                            .build())
                    .header(ACCEPT, "*/*")
                    .header(AUTHORIZATION, bearerToken)
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .retrieve()
                    .toEntity(RESPONSE_TYPE_OBJECT)
                    .block();

            return nonNull(response)
                    ? ResponseEntity.status(response.getStatusCode()).body(response.getBody())
                    : ResponseEntity.notFound().build();
        } catch (HttpStatusCodeException e) {
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
        } catch (HttpStatusCodeException e) {
            log.debug("Institusjon med tssEksternId {} er ikke gyldig på dato {}.", tssEksternId, date);
            return e.getStatusCode();
        }
    }

    public boolean isMiljoeTilgjengelig(String miljoe) {
        try {
            ResponseEntity<List<String>> response = webClient.get().uri(uriBuilder ->
                            uriBuilder.path(inst2NewServerUrl + "/v1/environment")
                                    .build())
                    .retrieve().toEntityList(String.class).block();
            List<String> miljoer = nonNull(response) ? response.getBody() : emptyList();
            log.info("Tilgjengelige inst2 miljøer: {}", String.join(",", miljoer));
            return miljoer.stream().anyMatch(env -> env.equals(miljoe));
        } catch (HttpStatusCodeException e) {
            log.warn("Inst2 er ikke tilgjengelig i miljø {}", miljoe);
            return false;
        }
    }
}
