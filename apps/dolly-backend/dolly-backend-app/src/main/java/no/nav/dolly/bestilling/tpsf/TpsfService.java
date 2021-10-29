package no.nav.dolly.bestilling.tpsf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.udistub.domain.RsAliasRequest;
import no.nav.dolly.bestilling.udistub.domain.RsAliasResponse;
import no.nav.dolly.config.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.domain.resultset.tpsf.CheckStatusResponse;
import no.nav.dolly.domain.resultset.tpsf.EnvironmentsResponse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfIdenterMiljoer;
import no.nav.dolly.domain.resultset.tpsf.TpsfRelasjonRequest;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class TpsfService {

    private static final String TPSF_BASE_URL = "/api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "personer";
    private static final String TPSF_SEND_TPS_FLERE_URL = "tilTpsFlere";
    private static final String TPSF_HENT_PERSONER_URL = "hentpersoner";
    private static final String TPSF_CHECK_IDENT_STATUS = "checkpersoner";
    private static final String TPSF_UPDATE_PERSON_URL = TPSF_BASE_URL + "/leggtilpaaperson";
    private static final String TPSF_CREATE_ALIASES = TPSF_BASE_URL + "/aliaser";
    private static final String TPSF_DELETE_PERSON_URL = TPSF_BASE_URL + "/person";
    private static final String TPSF_GET_ENVIRONMENTS = "/api/v1/environments";
    private static final String TPSF_PERSON_RELASJON = TPSF_BASE_URL + "/relasjonperson";
    private static final String TPSF_IMPORTER_PERSON = TPSF_BASE_URL + "/import/lagre";

    private static final String TPSF_IDENT_QUERY = "ident";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final NaisServerProperties serviceProperties;

    public TpsfService(TokenService tokenService, TpsForvalterenProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_getEnvironments" })
    public ResponseEntity<EnvironmentsResponse> getEnvironments() {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(TPSF_GET_ENVIRONMENTS).build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve().toEntity(EnvironmentsResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_deletePersons" })
    public ResponseEntity<Object> deletePerson(String ident) {

        return webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(TPSF_DELETE_PERSON_URL)
                        .queryParam(TPSF_IDENT_QUERY, ident).build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve().toEntity(Object.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_createAliases" })
    public ResponseEntity<RsAliasResponse> createAliases(RsAliasRequest request) {

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(TPSF_CREATE_ALIASES).build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(request)
                .retrieve().toEntity(RsAliasResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_checkEksisterendeIdenter" })
    public CheckStatusResponse checkEksisterendeIdenter(List<String> identer) {
        ResponseEntity<Object> response = postToTpsf(TPSF_CHECK_IDENT_STATUS, identer);
        return isBodyNotNull(response) ? objectMapper.convertValue(response.getBody(), CheckStatusResponse.class) : null;
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_opprettIdenter" })
    public List<String> opprettIdenterTpsf(TpsfBestilling request) {
        ResponseEntity<Object> response = postToTpsf(TPSF_OPPRETT_URL, request);
        return isBodyNotNull(response) ? objectMapper.convertValue(response.getBody(), List.class) : null;
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_sendIdenterTilMiljoe" })
    public RsSkdMeldingResponse sendIdenterTilTpsFraTPSF(List<String> identer, List<String> environments) {
        validateEnvironments(environments);
        ResponseEntity<Object> response = postToTpsf(TPSF_SEND_TPS_FLERE_URL, new TpsfIdenterMiljoer(identer, environments));
        return isBodyNotNull(response) ? objectMapper.convertValue(response.getBody(), RsSkdMeldingResponse.class) : null;
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_hentTestpersoner" })
    public List<Person> hentTestpersoner(List<String> identer) {
        if (identer.isEmpty()) {
            return emptyList();
        }
        ResponseEntity<Object> response = postToTpsf(TPSF_HENT_PERSONER_URL, identer);
        if (isBodyNotNull(response)) {
            return new ArrayList<>(List.of(objectMapper.convertValue(response.getBody(), Person[].class)));
        }
        return emptyList();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_leggTIlPaaPerson" })
    public RsOppdaterPersonResponse endreLeggTilPaaPerson(String ident, TpsfBestilling tpsfBestilling) {

        ResponseEntity<RsOppdaterPersonResponse> response = webClient.post().uri(uriBuilder -> uriBuilder
                        .path(TPSF_UPDATE_PERSON_URL)
                        .queryParam(TPSF_IDENT_QUERY, ident).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(tpsfBestilling)
                .retrieve().toEntity(RsOppdaterPersonResponse.class)
                .block();

        if (isNull(response) || !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å legge til på ident %s i tps-forvalteren", ident));
        }
        return response.getBody();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_relasjonPerson" })
    public List<String> relasjonPerson(String ident, TpsfRelasjonRequest tpsfBestilling) {

        ResponseEntity<List<String>> response = webClient.post().uri(uriBuilder -> uriBuilder
                        .path(TPSF_PERSON_RELASJON)
                        .queryParam(TPSF_IDENT_QUERY, ident).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(tpsfBestilling)
                .retrieve().toEntityList(String.class)
                .block();

        if (isNull(response) || !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å legge til på ident %s i tps-forvalteren", ident));
        }
        return response.getBody();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_hentPersonFraTps" })
    public Person importerPersonFraTps(TpsfImportPersonRequest tpsfImportPersonRequest) {

        ResponseEntity<Person> response = webClient.post().uri(uriBuilder -> uriBuilder
                        .path(TPSF_IMPORTER_PERSON).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(tpsfImportPersonRequest)
                .retrieve().toEntity(Person.class)
                .block();

        if (isNull(response) || !response.hasBody()) {
            throw new DollyFunctionalException("Klarte ikke å importere person fra tps-forvalteren");
        }
        return response.getBody();
    }

    private ResponseEntity<Object> postToTpsf(String addtionalUrl, Object request) {

        try {
            ResponseEntity<Object> response = webClient.post().uri(uriBuilder -> uriBuilder
                            .path(TPSF_BASE_URL)
                            .pathSegment(addtionalUrl).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .bodyValue(request)
                    .retrieve().toEntity(Object.class)
                    .block();

            if (isBodyNotNull(response) && (requireNonNull(response.getBody()).toString().contains("error="))) {
                WebClientResponseException rs = objectMapper.convertValue(response.getBody(), WebClientResponseException.class);
                log.error("Tps-forvalteren kall feilet mot url <{}> grunnet {}", TPSF_BASE_URL + addtionalUrl, rs.getMessage());
                throw new TpsfException(format("%s -- (%s %s)", rs.getMessage(), rs.getStatusText(), rs.getMessage()));
            }
            return response;

        } catch (WebClientException e) {
            log.error("Tps-forvalteren kall feilet mot url <{}> grunnet {}", TPSF_BASE_URL + addtionalUrl, e.getMessage());
            try {
                WebClientResponseException failure = objectMapper.readValue(e.getMessage().getBytes(UTF_8), WebClientResponseException.class);
                throw new TpsfException(format("%s -- (%s %s)", failure.getMessage(), failure.getStatusText(), failure.getMessage()), e);
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
            throw new TpsfException("Formattering av TPS-melding feilet.", e);
        }
    }

    private static boolean isBodyNotNull(ResponseEntity<Object> response) {
        return nonNull(response) && nonNull(response.getBody()) && isNotBlank(response.getBody().toString());
    }

    private void validateEnvironments(List<String> environments) {
        if (nonNull(environments) && environments.isEmpty()) {
            throw new IllegalArgumentException("Ingen TPS miljoer er spesifisert for sending av testdata");
        }
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}