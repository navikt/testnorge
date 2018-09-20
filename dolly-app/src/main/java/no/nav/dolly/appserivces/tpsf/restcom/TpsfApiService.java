package no.nav.dolly.appserivces.tpsf.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.appserivces.tpsf.errorhandling.RestTemplateFailure;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.RsTpsfIdenter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TpsfApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String TPSF_BASE_URL = "/api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "/personer";
    private static final String TPSF_SEND_TPS_URL = "/tilTps";
    private static final String TPSF_SEND_TPS_FLERE_URL = "/tilTpsFlere";

    @Autowired
    ObjectMapper objectMapper;

    @Value("${tpsf.server.url}")
    private String tpsfServerUrl;

    public List<String> opprettPersonerTpsf(RsTpsfBestilling request) {
        StringBuilder sbUrl = new StringBuilder().append(tpsfServerUrl).append(TPSF_BASE_URL).append(TPSF_OPPRETT_URL);


        System.out.println("#### OPPRETTER PERSONER ######   " + sbUrl.toString());
        System.out.println(request);
        ResponseEntity<Object> response = restTemplate.exchange(sbUrl.toString(), HttpMethod.POST, new HttpEntity<>(request), Object.class);

        System.out.println("#### RESPONSE ######   " + response.getBody());
        if (response.getBody().toString().contains("exception=")) {
            RestTemplateFailure rs = objectMapper.convertValue(response.getBody(), RestTemplateFailure.class);
            throw new TpsfException("TPSF kall feilet med: " + rs.getMessage() + "\\r\\n Feil: " + rs.getError());
        }

        return objectMapper.convertValue(response.getBody(), List.class);
    }

    //TODO fjern når testene er skrevet om.
    public RsSkdMeldingResponse sendTilTpsFraTPSF(String ident, List<String> environments) {
        StringBuilder sb = new StringBuilder();
        sb.append(tpsfServerUrl).append(TPSF_BASE_URL).append(TPSF_SEND_TPS_FLERE_URL);
        sb.append("?environments=");
        environments.forEach(env -> sb.append(env).append(","));
        String url = sb.toString().substring(0, sb.length() - 1);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(new RsTpsfIdenter(Arrays.asList(ident))), Object.class);

        if (response.getBody().toString().contains("exception=")) {
            RestTemplateFailure rs = objectMapper.convertValue(response.getBody(), RestTemplateFailure.class);
            throw new TpsfException("TPSF kall feilet med: " + rs.getMessage() + "\\r\\n Feil: " + rs.getError());
        }

        RsSkdMeldingResponse responseSkd = objectMapper.convertValue(response.getBody(), RsSkdMeldingResponse.class);

        return responseSkd;
    }

    public RsSkdMeldingResponse sendTilTpsFraTPSF(List<String> identer, List<String> environments) {
        StringBuilder sb = new StringBuilder();
        sb.append(tpsfServerUrl).append(TPSF_BASE_URL).append(TPSF_SEND_TPS_FLERE_URL);
        sb.append("?environments=");
        environments.forEach(env -> sb.append(env).append(","));
        String url = sb.toString().substring(0, sb.length() - 1);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(identer), Object.class);

        if (response.getBody().toString().contains("exception=")) {
            RestTemplateFailure rs = objectMapper.convertValue(response.getBody(), RestTemplateFailure.class);
            throw new TpsfException("TPSF kall feilet med: " + rs.getMessage() + "\\r\\n Feil: " + rs.getError());
        }

        RsSkdMeldingResponse responseSkd = objectMapper.convertValue(response.getBody(), RsSkdMeldingResponse.class);

        return responseSkd;
    }
}
