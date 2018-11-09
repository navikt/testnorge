package no.nav.dolly.bestilling.tpsf;

import static java.lang.String.format;
import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.errorhandling.RestTemplateFailure;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
public class TpsfService {

    private static final String TPSF_BASE_URL = "/api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "/personer";
    private static final String TPSF_SEND_TPS_FLERE_URL = "/tilTpsFlere";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProvidersProps providersProps;

    public List<String> opprettIdenterTpsf(RsTpsfBestilling request) {
        ResponseEntity<Object> response = postToTpsf(format("%s%s%s", providersProps.getTpsf().getUrl(), TPSF_BASE_URL, TPSF_OPPRETT_URL), new HttpEntity<>(request));
        return response != null ? objectMapper.convertValue(response.getBody(), List.class) : null;
    }

    public RsSkdMeldingResponse sendIdenterTilTpsFraTPSF(List<String> identer, List<String> environments) {
        validateEnvironments(environments);
        String url = buildTpsfUrlFromEnvironmentsInput(environments);
        ResponseEntity<Object> response = postToTpsf(url, new HttpEntity<>(identer));
        return response != null ? objectMapper.convertValue(response.getBody(), RsSkdMeldingResponse.class) : null;
    }

    private ResponseEntity<Object> postToTpsf(String url, HttpEntity request) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
            if (isBodyNotNull(response) && response.getBody().toString().contains("exception=")) {
                RestTemplateFailure rs = objectMapper.convertValue(response.getBody(), RestTemplateFailure.class);
                log.error("Tps-forvalteren kall feilet mot url <{}> grunnet {}", url, rs.getMessage());
                throw new TpsfException("TPSF kall feilet med: " + rs.getMessage() + "\\r\\n Feil: " + rs.getError());
            }
            return response;

        } catch (HttpClientErrorException e) {
            log.error("Tps-forvalteren kall feilet mot url <" + url + "> grunnet " + e.getMessage(), e);
            throw new TpsfException("TPSF kall feilet med: " + e.getMessage() + "\\r\\n Feil: " + e.getResponseBodyAsString(), e);
        }
    }

    boolean isBodyNotNull(ResponseEntity<Object> response) {
        return response != null && response.getBody() != null && response.getBody().toString() != null;
    }

    private void validateEnvironments(List<String> environments) {
        if (isNullOrEmpty(environments)) {
            throw new IllegalArgumentException("Ingen TPS miljoer er spesifisert for sending av testdata");
        }
    }

    private String buildTpsfUrlFromEnvironmentsInput(List<String> environments) {
        StringBuilder sb = new StringBuilder();
        sb.append(providersProps.getTpsf().getUrl()).append(TPSF_BASE_URL).append(TPSF_SEND_TPS_FLERE_URL).append("?environments=");
        environments.forEach(env -> sb
                .append(env)
                .append(','));
        return sb.substring(0, sb.length() - 1);
    }
}
