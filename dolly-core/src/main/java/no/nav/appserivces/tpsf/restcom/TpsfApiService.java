package no.nav.appserivces.tpsf.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.appserivces.tpsf.domain.request.RsDollyBestillingsRequest;
import no.nav.appserivces.tpsf.domain.request.RsTpsfIdent;
import no.nav.appserivces.tpsf.domain.response.RsSkdMeldingResponse;
import no.nav.appserivces.tpsf.errorHandling.RestTemplateException;
import no.nav.appserivces.tpsf.exceptions.TpsfErrorHandler;
import no.nav.exceptions.DollyFunctionalException;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TpsfApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String TPSF_BASE_URL = "/api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "/personer";
    private static final String TPSF_SEND_TPS_URL = "/tilTps";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MapperFacade mapperFacade;

    @Value("${tpsf.server.url}")
    private String tpsfServerUrl;

    public List<String> opprettPersonerTpsf(RsDollyBestillingsRequest request) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(tpsfServerUrl).append(TPSF_BASE_URL).append(TPSF_OPPRETT_URL);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<Object> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Object> response = restTemplate.exchange(sbUrl.toString(), HttpMethod.POST, entity, Object.class);

        if (response.getBody().toString().contains("exception=")) {
            RestTemplateException rs = objectMapper.convertValue(response.getBody(), RestTemplateException.class);
            throw new DollyFunctionalException("TPSF kall feilet med: " + rs.getMessage() + "\\r\\n Feil: " + rs.getError());
        }

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new DollyFunctionalException("TPSF kall feilet med: " + response.getBody());
        }

        List<String> ident = objectMapper.convertValue(response.getBody(), List.class);

        return ident;
    }

    public RsSkdMeldingResponse sendTilTpsFraTPSF(String ident, List<String> environments) throws DollyFunctionalException {
        StringBuilder sb = new StringBuilder();
        sb.append(tpsfServerUrl).append(TPSF_BASE_URL).append(TPSF_SEND_TPS_URL);
        sb.append("?environments=");
        environments.forEach(env -> sb.append(env).append(","));
        String url = sb.toString().substring(0, sb.length() - 1);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<Object> entity = new HttpEntity<>(new RsTpsfIdent(ident), headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);

        if (response.getBody().toString().contains("exception=")) {
            RestTemplateException rs = objectMapper.convertValue(response.getBody(), RestTemplateException.class);
            throw new DollyFunctionalException("TPSF kall feilet med: " + rs.getMessage() + "\\r\\n Feil: " + rs.getError());
        }

        RsSkdMeldingResponse responseSkd = objectMapper.convertValue(response.getBody(), RsSkdMeldingResponse.class);

        return responseSkd;
    }
}
