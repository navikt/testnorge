package no.nav.dolly.bestilling.tpsf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.errorhandling.RestTemplateFailure;
import no.nav.dolly.bestilling.udistub.domain.RsAliasRequest;
import no.nav.dolly.bestilling.udistub.domain.RsAliasResponse;
import no.nav.dolly.domain.resultset.tpsf.CheckStatusResponse;
import no.nav.dolly.domain.resultset.tpsf.EnvironmentsResponse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfIdenterMiljoer;
import no.nav.dolly.domain.resultset.tpsf.TpsfRelasjonRequest;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsfService {

    private static final String TPSF_BASE_URL = "/api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "/personer";
    private static final String TPSF_SEND_TPS_FLERE_URL = "/tilTpsFlere";
    private static final String TPSF_HENT_PERSONER_URL = "/hentpersoner";
    private static final String TPSF_CHECK_IDENT_STATUS = "/checkpersoner";
    private static final String TPSF_UPDATE_PERSON_URL = TPSF_BASE_URL + "/leggtilpaaperson?ident=";
    private static final String TPSF_CREATE_ALIASES = TPSF_BASE_URL + "/aliaser";
    private static final String TPSF_DELETE_PERSON_URL = TPSF_BASE_URL + "/person?ident=";
    private static final String TPSF_GET_ENVIRONMENTS = "/api/v1/environments";
    private static final String TPSF_PERSON_RELASJON = TPSF_BASE_URL + "/relasjonperson?ident=";
    private static final String TPSF_IMPORTER_PERSON = TPSF_BASE_URL + "/import/lagre";
    private static final String CONCAT_VALUES = "%s%s%s";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "tpsf_getEnvironments" })
    public ResponseEntity<EnvironmentsResponse> getEnvironments() {
        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getTpsf().getUrl() + TPSF_GET_ENVIRONMENTS))
                        .build(), EnvironmentsResponse.class);
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_deletePersons" })
    public ResponseEntity<Object> deletePerson(String ident) {
        return restTemplate.exchange(
                RequestEntity.delete(URI.create(format(CONCAT_VALUES, providersProps.getTpsf().getUrl(), TPSF_DELETE_PERSON_URL, ident)))
                        .build(), Object.class);
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_createAliases" })
    public ResponseEntity<RsAliasResponse> createAliases(RsAliasRequest request) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getTpsf().getUrl() + TPSF_CREATE_ALIASES))
                        .body(request), RsAliasResponse.class);
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
        ResponseEntity<Object> response = postToTpsf(TPSF_HENT_PERSONER_URL, identer);
        if (isBodyNotNull(response)) {
            return new ArrayList<>(List.of(objectMapper.convertValue(response.getBody(), Person[].class)));
        }
        return emptyList();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_leggTIlPaaPerson" })
    public RsOppdaterPersonResponse endreLeggTilPaaPerson(String ident, TpsfBestilling tpsfBestilling) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(format(CONCAT_VALUES, providersProps.getTpsf().getUrl(), TPSF_UPDATE_PERSON_URL, ident)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(tpsfBestilling), RsOppdaterPersonResponse.class).getBody();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_relasjonPerson" })
    public List<String> relasjonPerson(String ident, TpsfRelasjonRequest tpsfBestilling) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(format(CONCAT_VALUES, providersProps.getTpsf().getUrl(), TPSF_PERSON_RELASJON, ident)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(tpsfBestilling), List.class).getBody();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_hentPersonFraTps" })
    public Person importerPersonFraTps(TpsfImportPersonRequest tpsfImportPersonRequest) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getTpsf().getUrl() + TPSF_IMPORTER_PERSON))
                .contentType(MediaType.APPLICATION_JSON)
                .body(tpsfImportPersonRequest), Person.class).getBody();
    }

    private ResponseEntity<Object> postToTpsf(String addtionalUrl, Object request) {
        String url = format(CONCAT_VALUES, providersProps.getTpsf().getUrl(), TPSF_BASE_URL, addtionalUrl);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    RequestEntity.post(URI.create(url))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request), Object.class);

            if (isBodyNotNull(response) && (requireNonNull(response.getBody()).toString().contains("error="))) {
                RestTemplateFailure rs = objectMapper.convertValue(response.getBody(), RestTemplateFailure.class);
                log.error("Tps-forvalteren kall feilet mot url <{}> grunnet {}", url, rs.getMessage());
                throw new TpsfException(format("%s -- (%s %s)", rs.getMessage(), rs.getStatus(), rs.getError()));
            }
            return response;

        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Tps-forvalteren kall feilet mot url <{}> grunnet {}", url, e.getMessage());
            try {
                RestTemplateFailure failure = objectMapper.readValue(e.getResponseBodyAsString().getBytes(UTF_8), RestTemplateFailure.class);
                throw new TpsfException(format("%s -- (%s %s)", failure.getMessage(), failure.getStatus(), failure.getError()), e);
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
}