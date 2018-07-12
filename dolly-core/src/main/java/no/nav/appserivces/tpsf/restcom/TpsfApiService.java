package no.nav.appserivces.tpsf.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.appserivces.tpsf.domain.request.RsDollyPersonKriteriumRequest;
import no.nav.appserivces.tpsf.domain.response.RsSkdMeldingResponse;
import no.nav.exceptions.DollyFunctionalException;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
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

    @Value("${tpsf.server.url}")
    private String tpsfServerUrl;

    public List<Object> opprettPersonerTpsf(RsDollyPersonKriteriumRequest request){
        StringBuilder sb = new StringBuilder();
        sb.append(tpsfServerUrl).append(TPSF_BASE_URL).append(TPSF_OPPRETT_URL);

        //        HttpEntity<String> httpEntity = null;
        //        try {
        //            httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(request));
        //        } catch (Exception e){
        //            throw new DollyFunctionalException("Mapperfeil");
        //        }

        RsDollyPersonKriteriumRequest request1 = RsDollyPersonKriteriumRequest.builder()
                .antall(1)
                .foedtEtter(LocalDate.of(2010, 1, 7))
                .foedtFoer(LocalDate.now())
                .regdato(LocalDateTime.now())
                .identtype("FNR")
                .kjonn('M')
                .withAdresse(false)
                .statsborgerskap("NOR")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<Object> entity;
        try {
            Map<String, String> map = objectMapper.convertValue(request1, Map.class);
            entity = new HttpEntity<>(map, headers);
        } catch (Exception e){
            throw new DollyFunctionalException(e.getMessage());
        }

        ResponseEntity<Object[]> response = null;
        ResponseEntity<Object[]> respyy = restTemplate.getForEntity("http://localhost:8050/api/v1/dolly/testdata/personerdata?identer=13101750196", Object[].class);
        try {
            response = restTemplate.exchange("http://localhost:8050/api/v1/dolly/testdata/personer", HttpMethod.POST, entity, Object[].class);
        } catch (Exception e) {
            throw new DollyFunctionalException("TPSF exception ble kastet: " + e.getMessage());
        }

        if (response.getStatusCode() != HttpStatus.CREATED) {
            //throw IllegalArgumentException;
        }

        return Arrays.asList(response.getBody());
    }

    public RsSkdMeldingResponse sendTilTpsFraTPSF(String ident, List<String> environments) {
        StringBuilder sb = new StringBuilder();
        sb.append(tpsfServerUrl).append(TPSF_BASE_URL).append(TPSF_SEND_TPS_URL);
        sb.append("?ident=").append(ident);
        sb.append("&environments=");
        environments.forEach(env -> sb.append(env).append(","));
        String url = sb.toString().substring(0, sb.length() - 1);

        HttpEntity<String> httpEntity = new HttpEntity<>(new JSONObject().toJSONString());

        ResponseEntity<RsSkdMeldingResponse> response = restTemplate.postForEntity(url, httpEntity, RsSkdMeldingResponse.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            //throw IllegalArgumentException;
        }

        return response.getBody();
    }
}
