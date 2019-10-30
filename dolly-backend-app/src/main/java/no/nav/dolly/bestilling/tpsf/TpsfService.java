package no.nav.dolly.bestilling.tpsf;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.security.sts.StsOidcService.getUserIdToken;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.errorhandling.RestTemplateFailure;
import no.nav.dolly.bestilling.udistub.RsAliasRequest;
import no.nav.dolly.bestilling.udistub.RsAliasResponse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsfIdenterMiljoer;
import no.nav.dolly.domain.resultset.tpsf.CheckStatusResponse;
import no.nav.dolly.domain.resultset.tpsf.EnvironmentsResponse;
import no.nav.dolly.domain.resultset.tpsf.RsPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsfService {

    private static final String TPSF_BASE_URL = "/api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "/personer";
    private static final String TPSF_SEND_TPS_FLERE_URL = "/tilTpsFlere";
    private static final String TPSF_HENT_PERSONER_URL = "/hentpersoner";
    private static final String TPSF_CHECK_IDENT_STATUS = "/checkpersoner";
    private static final String TPSF_UPDATE_PERSON_URL = "/api/v1/testdata/updatepersoner";
    private static final String TPSF_CREATE_ALIASES = "/api/v1/dolly/testdata/aliaser";
    private static final String TPSF_DELETE_PERSONER_URL = TPSF_BASE_URL + "/personer?identer=";
    private static final String TPSF_GET_ENVIRONMENTS = "/api/v1/environments";

    private final ObjectMapper objectMapper;
    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public ResponseEntity<EnvironmentsResponse> getEnvironments() {
        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getTpsf().getUrl() + TPSF_GET_ENVIRONMENTS))
                        .build(), EnvironmentsResponse.class);
    }

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public ResponseEntity deletePersones(List<String> identer) {
        return restTemplate.exchange(
                RequestEntity.delete(URI.create(format("%s%s%s", providersProps.getTpsf().getUrl(), TPSF_DELETE_PERSONER_URL, join(",", identer))))
                        .build(), Object.class);
    }

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public ResponseEntity<RsAliasResponse> createAliases(RsAliasRequest request) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getTpsf().getUrl() + TPSF_CREATE_ALIASES))
                        .body(request), RsAliasResponse.class);
    }

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public CheckStatusResponse checkEksisterendeIdenter(List<String> identer) {
        ResponseEntity<Object> response = postToTpsf(TPSF_CHECK_IDENT_STATUS, new HttpEntity<>(identer));
        return isBodyNotNull(response) ? objectMapper.convertValue(response.getBody(), CheckStatusResponse.class) : null;
    }

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public List<String> opprettIdenterTpsf(TpsfBestilling request) {
        ResponseEntity<Object> response = postToTpsf(TPSF_OPPRETT_URL, new HttpEntity<>(request));
        return isBodyNotNull(response) ? objectMapper.convertValue(response.getBody(), List.class) : null;
    }

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public RsSkdMeldingResponse sendIdenterTilTpsFraTPSF(List<String> identer, List<String> environments) {
        validateEnvironments(environments);
        ResponseEntity<Object> response = postToTpsf(TPSF_SEND_TPS_FLERE_URL, new HttpEntity<>(new TpsfIdenterMiljoer(identer, environments)));
        return isBodyNotNull(response) ? objectMapper.convertValue(response.getBody(), RsSkdMeldingResponse.class) : null;
    }

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public List<String> hentTilhoerendeIdenter(List<String> identer) {
        List<String> identerMedFamilie = new ArrayList<>();
        ResponseEntity<Object> response = postToTpsf(TPSF_HENT_PERSONER_URL, new HttpEntity<List>(identer));
        if (isBodyNotNull(response)) {
            Person[] personer = objectMapper.convertValue(response.getBody(), Person[].class);

            asList(personer).forEach(person -> {
                identerMedFamilie.add(person.getIdent());
                person.getRelasjoner().forEach(relasjon -> identerMedFamilie.add(relasjon.getPersonRelasjonMed().getIdent()));
            });
        }
        return identerMedFamilie;
    }

    @Timed(name="providers", tags={"operation", "motTPSF"})
    public ResponseEntity updatePerson(RsPerson tpsfPerson) {
        return restTemplate.exchange(RequestEntity.post(URI.create(providersProps.getTpsf().getUrl() + TPSF_UPDATE_PERSON_URL))
                .header(AUTHORIZATION, getUserIdToken())
                .body(singletonList(tpsfPerson)), Object.class);
    }

    private ResponseEntity<Object> postToTpsf(String addtionalUrl, HttpEntity request) {
        String url = format("%s%s%s", providersProps.getTpsf().getUrl(), TPSF_BASE_URL, addtionalUrl);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
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